package com.port.portcoin.domain.external.coingecko.client;

import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CoinGeckoClient {
    private final RestClient restClient;

    public CoinGeckoClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<CoinMarketResponse> fetchCoinMarkets() {
        return restClient.get()
                .uri("/coins/markets?vs_currency=krw&order=market_cap_desc")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
