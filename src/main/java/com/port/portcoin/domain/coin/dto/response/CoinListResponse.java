package com.port.portcoin.domain.coin.dto.response;

import lombok.Getter;

@Getter
public class CoinListResponse {
    private Long id;
    private String symbol;


    public CoinListResponse(Long id, String symbol) {
        this.id = id;
        this.symbol = symbol;

    }
}
