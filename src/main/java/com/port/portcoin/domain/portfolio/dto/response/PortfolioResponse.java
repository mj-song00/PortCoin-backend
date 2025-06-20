package com.port.portcoin.domain.portfolio.dto.response;

import com.port.portcoin.domain.portfolio.entity.Portfolio;
import lombok.Getter;


@Getter
public class PortfolioResponse {
    private final String name;
    private final Long portfolioId;

    public PortfolioResponse(Portfolio portfolio){
        this.name = portfolio.getName();
        this.portfolioId = portfolio.getPortfolioId();
    }
}
