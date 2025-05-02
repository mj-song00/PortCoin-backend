package com.port.portcoin.domain.portfoliocoin.repository;

import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PortfolioCoinRepository extends JpaRepository<PortfolioCoin, Long> {
    List<PortfolioCoin> findByPortfolioUserId(UUID id);
}
