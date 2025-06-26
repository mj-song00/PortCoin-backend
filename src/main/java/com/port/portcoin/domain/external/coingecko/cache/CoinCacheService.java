package com.port.portcoin.domain.external.coingecko.cache;

import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CoinCacheService {
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;
    private final RedisTemplate<String, Double> priceRedisTemplate;

    public CoinCacheService(
            RedisTemplate<String, List<CoinMarketResponse>> redisTemplate,
            RedisTemplate<String, Double> priceRedisTemplate
    ) {
        this.redisTemplate = redisTemplate;
        this.priceRedisTemplate = priceRedisTemplate;
    }

    public List<CoinMarketResponse> getCachedMarketData() {
        return redisTemplate.opsForValue().get("CoinGeckoMarket:all_coins");
    }

    public void cacheMarketData(List<CoinMarketResponse> data) {
        redisTemplate.opsForValue().set("CoinGeckoMarket:all_coins", data, 1, TimeUnit.MINUTES);
    }

    public void cachePricePerSymbol(List<CoinMarketResponse> data) {
        for (CoinMarketResponse coin : data) {
            priceRedisTemplate.opsForValue().set("coin:price:" + coin.getId(), coin.getCurrentPrice(), 1, TimeUnit.MINUTES);
        }
    }
}
