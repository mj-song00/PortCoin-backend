package com.port.portcoin.domain.external;

import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CoinGecko {
    private final RestClient restClient;
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;


    public  CoinGecko(RestClient restClient,  RedisTemplate<String, List<CoinMarketResponse>> redisTemplate){
        this.restClient = restClient;
        this.redisTemplate = redisTemplate;
    }

    public List <CoinMarketResponse> getCoinList() {
        // Redis에서 먼저 데이터 조회
        List<CoinMarketResponse> cachedData = redisTemplate.opsForValue().get("CoinGeckoMarket:top10");

        if (cachedData == null) {
            // 캐시가 없으면 외부 API 호출
            cachedData = restClient.get()
                    .uri("/coins/markets?vs_currency=krw&order=market_cap_desc")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            // 데이터를 캐시에 저장
            redisTemplate.opsForValue().set("CoinGeckoMarket:top10", cachedData);
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
                .body(new ParameterizedTypeReference<>() {});

        // Redis에 새로운 데이터 저장
        redisTemplate.opsForValue().set("CoinGeckoMarket:top10", refreshedData);
    }
}
