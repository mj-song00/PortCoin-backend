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

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartService {

    private final CoinGecko coinGecko;
    private final UserRepository userRepository;
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;


    public CoinChartResponseWrapper getChart(CoinChartRequest coinChartRequest, AuthUser authUser) {
       User user = getUser(authUser.getId());
       if (!user.getId().equals(authUser.getId())) throw new BaseException(ExceptionEnum.USER_NOT_FOUND);

        CoinChartResponseWrapper original = coinGecko.getCoinChart(coinChartRequest.getSymbol(), coinChartRequest.getDays());

        // 2. Redis에 저장된 코인 시세 정보 조회
        List<CoinMarketResponse> allCoins = redisTemplate.opsForValue().get("CoinGeckoMarket:all_coins");

        if (allCoins == null) {
            throw new BaseException((ExceptionEnum.COIN_NOT_FOUND));
        }

        // 3. 해당 심볼의 priceChangePercentage24h 추출
        double priceChangePercentage24h = allCoins.stream()
                .filter(c -> c.getSymbol().equalsIgnoreCase(coinChartRequest.getSymbol()))
                .findFirst()
                .map(CoinMarketResponse::getPriceChangePercentage24h)
                .orElse(0.0);

        // 4. 차트 데이터에 값 추가하여 새로 구성
        List<CoinChartResponseWrapper.ChartPoint> enriched = original.getPrices().stream()
                .map(p -> new CoinChartResponseWrapper.ChartPoint(
                        LocalDate.parse(p.getDate()),
                        p.getPrice(),
                        p.getSymbol(),
                        p.getName(),
                        priceChangePercentage24h // ✅ 여기 추가
                ))
                .collect(Collectors.toList());

        return new CoinChartResponseWrapper(enriched);
    }


    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }
}
