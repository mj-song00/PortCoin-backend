package com.port.portcoin.domain.portfoliocoin.dto.response;

import lombok.Getter;

@Getter
public class CoinProfitLossResponse {
    private final String fullName;
    private final String image;
    private final double profitLoss;


    public CoinProfitLossResponse(String fullName, String image, double profitLoss){
        this.fullName = fullName;
        this.image = image;
        this.profitLoss = profitLoss;
    }

}
