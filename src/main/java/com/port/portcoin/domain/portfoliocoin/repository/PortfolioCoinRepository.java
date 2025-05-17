package com.port.portcoin.domain.portfoliocoin.repository;

import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PortfolioCoinRepository extends JpaRepository<PortfolioCoin, Long> {
    List<PortfolioCoin> findByPortfolioUserId(UUID id);

    @Modifying
    @Query("UPDATE PortfolioCoin pc SET pc.currentPrice = :currentPrice WHERE pc.coin.id = :coinId")
    void updateCurrentPriceByCoinId(Long coinId, double price);
}
