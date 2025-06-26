package com.port.portcoin.domain.portfoliocoin.dto.response;

import lombok.Getter;

@Getter
public class CoinProfitLossResponse {
    private final Long portfolioId;
    private final String fullName;
    private final String image;
    private final double profitLoss;


    public CoinProfitLossResponse(Long portfolioId, String fullName, String image, double profitLoss){
        this.portfolioId = portfolioId;
        this.fullName = fullName;
        this.image = image;
        this.profitLoss = profitLoss;
    }

}
