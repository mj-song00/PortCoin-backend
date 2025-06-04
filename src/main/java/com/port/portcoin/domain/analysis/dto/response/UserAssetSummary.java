package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

@Getter
public class UserAssetSummary {
    private Long userId;
    private Double totalAsset;

    public UserAssetSummary(Long userId, Double totalAsset) {
        this.userId = userId;
        this.totalAsset = totalAsset;
    }
}
