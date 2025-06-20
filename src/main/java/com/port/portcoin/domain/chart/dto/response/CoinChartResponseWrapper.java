package com.port.portcoin.domain.chart.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class CoinChartResponseWrapper {
    private List<ChartPoint> prices;

    public CoinChartResponseWrapper(List<ChartPoint> chartPoints) {
        this.prices = chartPoints;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartPoint {
        private String date;
        private double price;
        private String symbol;
        private String name;
        private double priceChangePercentage24h;

        public ChartPoint(LocalDate date, double price, String symbol,
                          String name, double priceChangePercentage24h) {
            this.date = date.toString(); // "2024-06-20"
            this.price = price;
            this.symbol = symbol;
            this.name = name;
            this.priceChangePercentage24h = priceChangePercentage24h;
        }
    }
}