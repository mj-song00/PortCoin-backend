package com.port.portcoin.domain.chart.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.chart.dto.request.CoinChartRequest;
import com.port.portcoin.domain.chart.dto.response.CoinChartResponseWrapper;
import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.external.coingecko.CoinGecko;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartService {

    private final CoinGecko coinGecko;
    private final UserRepository userRepository;
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;


    public Map<String, CoinChartResponseWrapper> getChart(CoinChartRequest coinChartRequest, AuthUser authUser) {
        // 1. 사용자 검증
        User user = getUser(authUser.getId());
        if (!user.getId().equals(authUser.getId())) {
            throw new BaseException(ExceptionEnum.USER_NOT_FOUND);
        }

        // 2. 심볼 리스트와 일 수 추출
        List<String> symbols = coinChartRequest.getSymbols();
        int days = coinChartRequest.getDays();

        // 3. 원본 차트 데이터 가져오기 (심볼별 차트 데이터 Map)
        Map<String, CoinChartResponseWrapper> original = coinGecko.getCoinChart(symbols, days);

        // 4. Redis에서 전체 코인 정보 조회
        List<CoinMarketResponse> allCoins = redisTemplate.opsForValue().get("CoinGeckoMarket:all_coins");
        if (allCoins == null) {
            throw new BaseException(ExceptionEnum.COIN_NOT_FOUND);
        }

        // 5. symbol + id 조합을 키로 24h 변동률 맵 생성
        Map<String, Double> priceChangeMap = allCoins.stream()
                .filter(c -> symbols.stream().anyMatch(s -> s.equalsIgnoreCase(c.getSymbol())))
                .collect(Collectors.toMap(
                        c -> c.getSymbol().toLowerCase() + ":" + c.getId(),
                        CoinMarketResponse::getPriceChangePercentage24h
                ));

        // 6. 차트 데이터에 변동률 추가하여 enriched Map 구성
        Map<String, CoinChartResponseWrapper> resultMap = new HashMap<>();

        original.forEach((symbol, wrapper) -> {
            List<CoinChartResponseWrapper.ChartPoint> enriched = wrapper.getPrices().stream()
                    .map(p -> {
                        // id는 없으니 symbol+id 형태의 key 만들기가 어려워서 아래는 symbol만으로 priceChange 조회
                        // 만약 ChartPoint에 id 정보가 있다면, 아래 key도 symbol+id로 만들어야 정확함
                        // 임시로 symbol만 사용해서 변동률 가져옴
                        double priceChange = priceChangeMap.entrySet().stream()
                                .filter(e -> e.getKey().startsWith(p.getSymbol().toLowerCase() + ":"))
                                .map(Map.Entry::getValue)
                                .findFirst()
                                .orElse(0.0);

                        return new CoinChartResponseWrapper.ChartPoint(
                                p.getDate(),
                                p.getPrice(),
                                p.getSymbol(),
                                p.getName(),
                                priceChange
                        );
                    })
                    .collect(Collectors.toList());

            resultMap.put(symbol, new CoinChartResponseWrapper(enriched));
        });

        return resultMap;
    }


    private User getUser(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }
}
