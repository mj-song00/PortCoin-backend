package com.port.portcoin.domain.portfolio.repository;

import com.port.portcoin.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public  interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    @Query("SELECT p FROM Portfolio p " +
            "LEFT JOIN FETCH p.portfolioCoins pc " +
            "LEFT JOIN FETCH pc.coin " +
            "WHERE p.portfolioId = :portfolioId")
    Optional<Portfolio> findByPortfolioId(Long portfolioId);

    List<Portfolio> findAllByUserIdAndDeletedAtIsNull(UUID id);
}
