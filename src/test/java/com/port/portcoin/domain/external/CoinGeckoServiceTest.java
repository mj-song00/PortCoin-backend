package com.port.portcoin.domain.external;

import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.external.coingecko.cache.CoinCacheService;
import com.port.portcoin.domain.external.coingecko.client.CoinGeckoClient;
import com.port.portcoin.domain.external.coingecko.service.CoinGeckoService;
import com.port.portcoin.domain.external.coingecko.update.CoinPriceUpdate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoinGeckoServiceTest {

    @Mock
    CoinGeckoClient client;

    @Mock
    CoinCacheService cacheService;

    @Mock
    CoinPriceUpdate priceUpdate;

    @InjectMocks
    CoinGeckoService coinGeckoService;

    @Test
    void getCoinList_캐시에_존재할_경우_캐시_데이터_리턴하고_API_호출안함() {
        // given
        List<CoinMarketResponse> cachedData =  List.of(new CoinMarketResponse("btc","Bitcoin","url",1L,145299326.44,0.12,2896155034069728L));
        when(cacheService.getCachedMarketData()).thenReturn(cachedData);

        // when
        List<CoinMarketResponse> result = coinGeckoService.getCoinList();

        // then
        assertEquals(cachedData, result);
        verify(cacheService).getCachedMarketData();
        verifyNoMoreInteractions(client, priceUpdate, cacheService);
    }

    @Test
    void getCoinList_캐시에_없을_경우_API_호출_후_캐시_저장_및_업데이트() {
        // given
        when(cacheService.getCachedMarketData()).thenReturn(null);

        List<CoinMarketResponse> freshData = List.of(new CoinMarketResponse("btc","Bitcoin","url",1L,145299326.44,0.12,2896155034069728L));
        when(client.fetchCoinMarkets()).thenReturn(freshData);

        // when
        List<CoinMarketResponse> result = coinGeckoService.getCoinList();

        // then
        assertEquals(freshData, result);
        verify(client).fetchCoinMarkets();
        verify(cacheService).cacheMarketData(freshData);
        verify(cacheService).cachePricePerSymbol(freshData);
        verify(priceUpdate).updatePortfolioCoinPrices(freshData);
    }
}