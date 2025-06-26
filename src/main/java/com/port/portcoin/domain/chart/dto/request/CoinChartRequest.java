package com.port.portcoin.domain.chart.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoinChartRequest {
    private List<String> symbols;
    private int days;
}
