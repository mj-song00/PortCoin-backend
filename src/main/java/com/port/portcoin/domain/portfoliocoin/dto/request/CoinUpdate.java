package com.port.portcoin.domain.portfoliocoin.dto.request;


import lombok.Data;

@Data
public class CoinUpdate {
    private Long portfolioCoinId;
    private Double amount;
    private Double purchasePrice;
}
