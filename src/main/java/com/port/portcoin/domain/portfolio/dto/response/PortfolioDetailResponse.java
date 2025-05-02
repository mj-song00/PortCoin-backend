package com.port.portcoin.domain.portfolio.dto.response;

import com.port.portcoin.domain.portfolio.entity.Portfolio;
import com.port.portcoin.domain.portfoliocoin.dto.response.PortfolioCoinResponse;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PortfolioDetailResponse {
    private final Long portfolioId;
    private final String name;
    private final  List<PortfolioCoinResponse> coins;

    public PortfolioDetailResponse(Portfolio portfolio){
        this.portfolioId = portfolio.getPortfolioId();
        this.name = portfolio.getName();
        this.coins = portfolio.getPortfolioCoins().stream()
                .map(PortfolioCoinResponse::new)
                .collect(Collectors.toList());
    }
}
