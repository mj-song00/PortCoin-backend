package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

@Getter
public class TotalAssetResponse {
    private double totalAssetValue;

    public TotalAssetResponse(double totalAssetValue) {
        this.totalAssetValue = totalAssetValue;
    }
}
