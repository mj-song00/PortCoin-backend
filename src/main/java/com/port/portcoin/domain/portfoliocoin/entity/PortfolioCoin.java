package com.port.portcoin.domain.portfoliocoin.entity;

import com.port.portcoin.common.entity.Timestamped;
import com.port.portcoin.domain.coin.entity.Coin;
import com.port.portcoin.domain.portfolio.entity.Portfolio;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@Table(name = "portfolio_coin")
@Getter
public class PortfolioCoin extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private Double purchasePrice;

    private Double currentPrice;

    private String purchaseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coin_id")
    private Coin coin;

    public  PortfolioCoin(Portfolio portfolio, Coin coin, Double amount, Double purchasePrice, Double currentPrice, String purchaseDate) {
        this.portfolio = portfolio;
        this.coin = coin;
        this.amount = amount;
        this.purchasePrice = purchasePrice;
        this.currentPrice = currentPrice;
        this.purchaseDate = purchaseDate;
    }

    public void update(Double amount, Double purchasePrice) {
        this.amount = amount;
        this.purchasePrice = purchasePrice;
    }

    public void assignPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public void updateCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }
}
