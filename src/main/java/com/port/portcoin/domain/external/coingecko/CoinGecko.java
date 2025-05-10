package com.port.portcoin.domain.external.coingecko;

import com.port.portcoin.domain.chart.dto.response.CoinChartResponse;
import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CoinGecko {
    private final RestClient restClient;
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;
    private final RedisTemplate<String, List<CoinChartResponse.ChartPoint>> chartRedisTemplate;

    public CoinGecko(RestClient restClient, RedisTemplate<String, List<CoinMarketResponse>> redisTemplate,
                     RedisTemplate<String, List<CoinChartResponse.ChartPoint>> chartRedisTemplate) {
        this.restClient = restClient;
        this.redisTemplate = redisTemplate;
        this.chartRedisTemplate = chartRedisTemplate;

    }

    public List<CoinMarketResponse> getCoinList() {
        // Redis에서 먼저 데이터 조회
        List<CoinMarketResponse> cachedData = redisTemplate.opsForValue().get("CoinGeckoMarket:top10");

        if (cachedData == null) {
            // 캐시가 없으면 외부 API 호출
            cachedData = restClient.get()
                    .uri("/coins/markets?vs_currency=krw&order=market_cap_desc")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            // 데이터를 캐시에 저장
            redisTemplate.opsForValue().set("CoinGeckoMarket:top10", cachedData, 1, TimeUnit.MINUTES);
        }

        return cachedData;
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
        redisTemplate.opsForValue().set("CoinGeckoMarket:top10", refreshedData, 1, TimeUnit.MINUTES);
    }

    public CoinChartResponse getCoinChart(String symbol, int days) {
        String cacheKey = "CoinChart:" + symbol;
        List<CoinChartResponse.ChartPoint> fullData = chartRedisTemplate.opsForValue().get(cacheKey);

        if (fullData == null) {
            // 1. JSON 파싱을 위한 중간 매핑 객체로 Map 사용
            Map<String, List<List<Object>>> rawData = restClient.get()
                    .uri("/coins/{symbol}/market_chart?vs_currency=krw&days=365", symbol)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            List<List<Object>> prices = rawData.get("prices");

            // 2. ChartPoint 리스트로 변환
            fullData = prices.stream()
                    .map(item -> {
                        long timestamp = ((Number) item.get(0)).longValue();
                        double price = ((Number) item.get(1)).doubleValue();
                        LocalDate date = Instant.ofEpochMilli(timestamp)
                                .atZone(ZoneId.of("Asia/Seoul"))
                                .toLocalDate();
                        return new CoinChartResponse.ChartPoint(date, price);
                    })
                    .collect(Collectors.toList());

            // 3. Redis에 캐싱
            chartRedisTemplate.opsForValue().set(cacheKey, fullData, 1, TimeUnit.DAYS);
        }

        // 4. 최근 날짜 기준으로 days 만큼 자르기
        List<CoinChartResponse.ChartPoint> resultData = fullData.stream()
                .sorted(Comparator.comparing(CoinChartResponse.ChartPoint::getDate).reversed())
                .limit(days)
                .sorted(Comparator.comparing(CoinChartResponse.ChartPoint::getDate)) // 오름차순
                .collect(Collectors.toList());

        return new CoinChartResponse(resultData);
    }

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다
    public void refreshAllTopCoinsCache() {
        // 1. Redis에서 캐싱된 전체 코인 목록 조회
        List<CoinMarketResponse> coinList = redisTemplate.opsForValue().get("CoinGeckoMarket:top10");

        if (coinList == null) {
            log.warn("전체 코인 목록이 캐시되어 있지 않습니다.");
            return;
        }

        for (CoinMarketResponse coin : coinList) {
            String symbol = coin.getSymbol(); // 주의: id를 사용해야 API에 들어감 (예: "bitcoin")

            try {
                Map<String, List<List<Object>>> rawData = restClient.get()
                        .uri("/coins/{symbol}/market_chart?vs_currency=krw&days=365", symbol)
                        .retrieve()
                        .body(new ParameterizedTypeReference<>() {});

                List<List<Object>> prices = rawData.get("prices");

                List<CoinChartResponse.ChartPoint> data = prices.stream()
                        .map(item -> {
                            long timestamp = ((Number) item.get(0)).longValue();
                            double price = ((Number) item.get(1)).doubleValue();
                            LocalDate date = Instant.ofEpochMilli(timestamp)
                                    .atZone(ZoneId.of("Asia/Seoul"))
                                    .toLocalDate();
                            return new CoinChartResponse.ChartPoint(date, price);
                        })
                        .collect(Collectors.toList());

                chartRedisTemplate.opsForValue().set("CoinChart:" + symbol, data, 1, TimeUnit.DAYS);
            } catch (Exception e) {
                log.warn("캐싱 실패: {}", symbol, e.toString());
            }
        }
    }
}
