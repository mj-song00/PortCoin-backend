package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

@Getter
public class AssetTableResponseItem {
    private String symbol;
    private String name;
    private double totalAmount;
    private double avgCurrentPrice;
    private double totalAssetValue;

    public AssetTableResponseItem(String symbol, String name, double totalAmount, double avgCurrentPrice, double totalAssetValue) {
        this.symbol = symbol;
        this.name = name;
        this.totalAmount = totalAmount;
        this.avgCurrentPrice = avgCurrentPrice;
        this.totalAssetValue = totalAssetValue;
    }
}
