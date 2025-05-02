package com.port.portcoin.domain.coin.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CoinMarketResponse {
    private String symbol;
    private String name;
    private String image;

    @JsonProperty("current_price")
    private double currentPrice;

    @JsonProperty("price_change_percentage_24h")
    private double priceChangePercentage24h;

    @JsonProperty("total_volume")
    private double totalVolume;

}
