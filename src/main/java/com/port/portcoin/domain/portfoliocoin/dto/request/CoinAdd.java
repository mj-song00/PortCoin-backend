package com.port.portcoin.domain.portfoliocoin.dto.request;

import lombok.Data;

@Data
public class CoinAdd {
    private Long coinId;
    private Double amount;
    private Double purchasePrice;
}
