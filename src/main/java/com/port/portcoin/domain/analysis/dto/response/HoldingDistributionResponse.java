package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

@Getter
public class HoldingDistributionResponse {
    private String range;
    private long userCount;
    private double percentage;

    public HoldingDistributionResponse(String range, long userCount, double percentage){
        this.range = range;
        this.userCount = userCount;
        this.percentage = percentage;
    }
}
