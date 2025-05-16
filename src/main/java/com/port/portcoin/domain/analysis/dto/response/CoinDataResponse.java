package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

@Getter
public class CoinDataResponse {
    private String symbol;
    private Double sum;

    public CoinDataResponse(String symbol, Double sum){
        this.symbol = symbol;
        this.sum = sum;
    }
}
