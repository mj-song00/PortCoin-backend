package com.port.portcoin.domain.coin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CoinMarketResponse {
    private String symbol;
    private String name;
    private String image;
    private String id;

    @JsonProperty("current_price")
    private double currentPrice;

    @JsonProperty("price_change_percentage_24h")
    private double priceChangePercentage24h;

    @JsonProperty("total_volume")
    private double totalVolume;

    public CoinMarketResponse(String symbol, String name, String image, String id, double currentPrice, double priceChangePercentage24h, double totalVolume) {
        this.symbol = symbol;
        this.name = name;
        this.image = image;
        this.id = id;
        this.currentPrice = currentPrice;
        this.priceChangePercentage24h = priceChangePercentage24h;
        this.totalVolume = totalVolume;
    }
}
