package com.port.portcoin.domain.portfolio.repository;

import com.port.portcoin.domain.portfolio.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public  interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByPortfolioId(Long portfolioId);

    List<Portfolio> findAllByUserId(UUID id);
}
