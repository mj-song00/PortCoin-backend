package com.port.portcoin.domain.portfoliocoin.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CoinAdd {
    private Long coinId;
    private Double amount;
    private Double purchasePrice;
    private Double currentPrice;
    private LocalDateTime purchaseDate;
}
