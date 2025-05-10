package com.port.portcoin.domain.chart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class CoinChartResponse {
    List<ChartPoint> prices;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartPoint{
        private LocalDate date;
        private double price;

    }
}
