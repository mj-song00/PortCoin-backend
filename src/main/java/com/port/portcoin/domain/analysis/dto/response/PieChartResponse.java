package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

@Getter
public class PieChartResponse {
    private String symbol;
    private String name;
    private double totalAssetValue; // 이 코인의 전체 자산 가치
    private double ratio; // 전체 자산 대비 비율 (퍼센트 계산용)

    public PieChartResponse(String symbol, String name, double totalAssetValue, double ratio) {
        this.symbol = symbol;
        this.name = name;
        this.totalAssetValue = totalAssetValue;
        this.ratio = ratio;
    }
}
