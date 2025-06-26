package com.port.portcoin.domain.portfoliocoin.repository;

import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PortfolioCoinRepository extends JpaRepository<PortfolioCoin, Long> {

    @Modifying
    @Query("UPDATE PortfolioCoin pc SET pc.currentPrice = :currentPrice WHERE pc.coin.id = :coinId")
    void updateCurrentPriceByCoinId(Long coinId, double price);

    List<PortfolioCoin> findByPortfolio_PortfolioIdAndPortfolioDeletedAtIsNull(Long portfolioId);

    boolean  existsByPortfolio_PortfolioIdAndCoin_Symbol(Long portfolioId, String symbol);
}
