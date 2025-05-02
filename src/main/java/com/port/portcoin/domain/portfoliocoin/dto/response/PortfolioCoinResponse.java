package com.port.portcoin.domain.portfoliocoin.dto.response;

import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import lombok.Getter;

@Getter
public class PortfolioCoinResponse {
    private final Long portfolioCoinId;  // 중간 테이블 id
    private final Long id;
    private final String symbol;
    private final String name;
    private final Double amount;
    private final Double purchasePrice;

    public PortfolioCoinResponse(PortfolioCoin pc) {
        this.portfolioCoinId = pc.getId();  // PortfolioCoin의 PK
        this.id = pc.getCoin().getId();
        this.symbol = pc.getCoin().getSymbol();
        this.name = pc.getCoin().getName();
        this.amount = pc.getAmount();
        this.purchasePrice = pc.getPurchasePrice();
    }
}
