package com.port.portcoin.domain.external.coingecko;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.chart.dto.response.CoinChartResponseWrapper;
import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.coin.repository.CoinRepository;
import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import com.port.portcoin.domain.portfoliocoin.repository.PortfolioCoinRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CoinGecko {
    private final RestClient restClient;
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;
    private final RedisTemplate<String, List<CoinChartResponseWrapper.ChartPoint>> chartRedisTemplate;
    private final RedisTemplate<String, Double> priceRedisTemplate;
    private final CoinRepository coinRepository;
    private final PortfolioCoinRepository portfolioCoinRepository;

    public CoinGecko(RestClient restClient,
                     RedisTemplate<String, List<CoinMarketResponse>> redisTemplate,
                     RedisTemplate<String, List<CoinChartResponseWrapper.ChartPoint>> chartRedisTemplate,
                     RedisTemplate<String, Double> priceRedisTemplate,
                     CoinRepository coinRepository, PortfolioCoinRepository portfolioCoinRepository) {
        this.restClient = restClient;
        this.redisTemplate = redisTemplate;
        this.chartRedisTemplate = chartRedisTemplate;
        this.priceRedisTemplate = priceRedisTemplate;
        this.coinRepository = coinRepository;
        this.portfolioCoinRepository = portfolioCoinRepository;
    }

    // 1분마다 캐시 갱신
    @Scheduled(fixedRate = 60000) // 60,000ms = 1분
    public void refreshCoinCache() {
        // 외부 API에서 데이터를 가져와서 Redis에 갱신
        List<CoinMarketResponse> refreshedData = restClient.get()
                .uri("/coins/markets?vs_currency=krw&order=market_cap_desc")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        // Redis에 새로운 데이터 저장
        redisTemplate.opsForValue().set("CoinGeckoMarket:all_coins", refreshedData, 1, TimeUnit.MINUTES);


        // 코인 심볼 → 현재가격 맵으로 변환해서 저장
        Map<String, String> priceMap = refreshedData.stream()
                .collect(Collectors.toMap(
                        c -> c.getSymbol().toUpperCase(),
                        c -> String.valueOf(c.getCurrentPrice()),
                        (existing, replacement) -> replacement // 중복 키가 있을 때 나중 값을 선택
                ));
    }

    @Scheduled(fixedRate = 60000) // 1분마다
    @Transactional
    public void syncRedisPriceToRdb() {
        // Redis에서 가격 정보 가져오기
        Map<Object, Object> priceMap = redisTemplate.opsForHash().entries("CoinMarket:prices");

        // coin_id, symbol 가져오기
        List<Object[]> coinIdAndSymbols = coinRepository.findAllCoinIdAndSymbol(); // (coinId, symbol)

        for (Object[] entry : coinIdAndSymbols) {
            Long coinId = (Long) entry[0];
            String symbol = ((String) entry[1]).toUpperCase();

            Object priceObj = priceMap.get(symbol);
            if (priceObj == null) continue;

            double price = Double.parseDouble(priceObj.toString());

            // portfolio_coin 테이블에 현재 가격 업데이트
            portfolioCoinRepository.updateCurrentPriceByCoinId(coinId, price);
        }
    }

    public Map<String, CoinChartResponseWrapper> getCoinChart(List<String> symbols, int days) {
        // 1. Redis에서 전체 코인 기본 정보 조회
        List<CoinMarketResponse> coinList = redisTemplate.opsForValue().get("CoinGeckoMarket:all_coins");
        if (coinList == null) {
            throw new BaseException(ExceptionEnum.COIN_NOT_FOUND);
        }

        // 2. symbol(소문자) -> CoinMarketResponse 매핑 (중복 심볼 첫 번째만 저장)
        Map<String, CoinMarketResponse> symbolToCoin = new HashMap<>();
        for (CoinMarketResponse coin : coinList) {
            symbolToCoin.putIfAbsent(coin.getSymbol().toLowerCase(), coin);
        }

        Map<String, CoinChartResponseWrapper> resultMap = new HashMap<>();

        // 3. 각 심볼별로 차트 데이터 조회 및 캐싱 처리
        for (String symbol : symbols) {
            CoinMarketResponse coin = symbolToCoin.get(symbol.toLowerCase());
            if (coin == null) {
                throw new BaseException(ExceptionEnum.COIN_NOT_FOUND);
            }

            String coinId = coin.getId();
            String name = coin.getName();
            double priceChangePercentage24h = coin.getPriceChangePercentage24h();

            // 캐시 키는 고유한 coinId를 사용
            String cacheKey = "CoinGeckoMarket:all_coins:" + coinId;

            // Redis에서 차트 데이터 조회
            List<CoinChartResponseWrapper.ChartPoint> fullData = chartRedisTemplate.opsForValue().get(cacheKey);

            if (fullData == null) {
                // 외부 API 호출해서 차트 데이터 조회
                Map<String, List<List<Object>>> rawData = restClient.get()
                        .uri("/coins/{id}/market_chart?vs_currency=krw&days=365", coinId)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

                List<List<Object>> prices = rawData.get("prices");

                fullData = prices.stream()
                        .map(item -> {
                            long timestamp = ((Number) item.get(0)).longValue();
                            double price = ((Number) item.get(1)).doubleValue();
                            LocalDate date = Instant.ofEpochMilli(timestamp)
                                    .atZone(ZoneId.of("Asia/Seoul"))
                                    .toLocalDate();
                            return new CoinChartResponseWrapper.ChartPoint(date, price, symbol, name, priceChangePercentage24h);
                        })
                        .collect(Collectors.toList());

                // Redis에 캐시 저장 (1일간)
                chartRedisTemplate.opsForValue().set(cacheKey, fullData, 1, TimeUnit.DAYS);
            }

            // 4. 최근 days 만큼만 자르고 정렬 (오름차순)
            List<CoinChartResponseWrapper.ChartPoint> resultData = fullData.stream()
                    .sorted(Comparator.comparing(CoinChartResponseWrapper.ChartPoint::getDate).reversed())
                    .limit(days)
                    .sorted(Comparator.comparing(CoinChartResponseWrapper.ChartPoint::getDate))
                    .collect(Collectors.toList());

            // 5. 결과 맵에 심볼 키로 저장 (소문자)
            resultMap.put(symbol.toLowerCase(), new CoinChartResponseWrapper(resultData));
        }

        // 6. 심볼별 차트 데이터 맵 반환
        return resultMap;
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다
    public void refreshAllTopCoinsCache() {
        List<CoinMarketResponse> coinList = redisTemplate.opsForValue().get("CoinGeckoMarket:all_coins");

        if (coinList == null) {
            log.warn("전체 코인 목록이 캐시되어 있지 않습니다.");
            return;
        }

        for (CoinMarketResponse coin : coinList) {
            String symbol = coin.getSymbol();
            String name = coin.getName();
            double priceChangePercentage24h = coin.getPriceChangePercentage24h();
            try {
                Map<String, List<List<Object>>> rawData = restClient.get()
                        .uri("/coins/{symbol}/market_chart?vs_currency=krw&days=365", symbol)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {
                        });

                List<List<Object>> prices = rawData.get("prices");

                List<CoinChartResponseWrapper.ChartPoint> data = prices.stream()
                        .map(item -> {
                            long timestamp = ((Number) item.get(0)).longValue();
                            double price = ((Number) item.get(1)).doubleValue();
                            LocalDate date = Instant.ofEpochMilli(timestamp)
                                    .atZone(ZoneId.of("Asia/Seoul"))
                                    .toLocalDate();
                            return new CoinChartResponseWrapper.ChartPoint(date, price, symbol, name, priceChangePercentage24h);
                        })
                        .collect(Collectors.toList());

                chartRedisTemplate.opsForValue().set("CoinChart:" + symbol, data, 1, TimeUnit.DAYS);
            } catch (Exception e) {
                log.warn("캐싱 실패: {}", symbol, e.toString());
            }
        }
    }
}
