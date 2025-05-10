package com.port.portcoin.domain.chart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinChartRequest {
    private String symbol;
    private int days;
}
