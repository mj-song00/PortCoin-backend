package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserProfitResponseItem {
    private UUID userId;
    private double profitRate;
    private Long portfolioId;

    public UserProfitResponseItem(UUID userId, double profitRate, Long portfolioId){
        this.userId = userId;
        this.profitRate = profitRate;
        this.portfolioId = portfolioId;
    }
}
