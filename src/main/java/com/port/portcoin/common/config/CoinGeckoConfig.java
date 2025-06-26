package com.port.portcoin.common.config;

import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.external.coingecko.cache.CoinCacheService;
import com.port.portcoin.domain.external.coingecko.client.CoinGeckoClient;
import com.port.portcoin.domain.external.coingecko.service.CoinGeckoService;
import com.port.portcoin.domain.external.coingecko.update.CoinPriceUpdate;
import com.port.portcoin.domain.portfoliocoin.repository.PortfolioCoinRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CoinGeckoConfig {

    @Bean
    public CoinGeckoClient coinGeckoClient(RestClient restClient) {
        return new CoinGeckoClient(restClient);
    }

    @Bean
    public CoinCacheService coinCacheService(
            RedisTemplate<String, List<CoinMarketResponse>> redisTemplate,
            RedisTemplate<String, Double> priceRedisTemplate
    ) {
        return new CoinCacheService(redisTemplate, priceRedisTemplate);
    }

    @Bean
    public CoinPriceUpdate coinPriceUpdater(PortfolioCoinRepository repository) {
        return new CoinPriceUpdate(repository);
    }

    @Bean
    public CoinGeckoService coinGeckoService(
            CoinGeckoClient client,
            CoinCacheService cache,
            CoinPriceUpdate updater
    ) {
        return new CoinGeckoService(client, cache, updater);
    }
}
