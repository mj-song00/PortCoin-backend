package com.port.portcoin.domain.external.coingecko.service;

import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.external.coingecko.cache.CoinCacheService;
import com.port.portcoin.domain.external.coingecko.client.CoinGeckoClient;
import com.port.portcoin.domain.external.coingecko.update.CoinPriceUpdate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
    public class CoinGeckoService {
        private final CoinGeckoClient client;
        private final CoinCacheService cacheService;
        private final CoinPriceUpdate priceUpdate;

        public CoinGeckoService(
            CoinGeckoClient client,
            CoinCacheService cacheService,
            CoinPriceUpdate priceUpdate
    ) {
        this.client = client;
        this.cacheService = cacheService;
        this.priceUpdate = priceUpdate;
    }

    public List<CoinMarketResponse> getCoinList() {
        List<CoinMarketResponse> cached = cacheService.getCachedMarketData();
        if (cached != null) return cached;

        List<CoinMarketResponse> fresh = client.fetchCoinMarkets();

        cacheService.cacheMarketData(fresh);
        cacheService.cachePricePerSymbol(fresh);
        priceUpdate.updatePortfolioCoinPrices(fresh);

        return fresh;
    }
}
