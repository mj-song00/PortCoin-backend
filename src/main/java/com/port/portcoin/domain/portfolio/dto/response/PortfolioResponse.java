package com.port.portcoin.domain.portfolio.dto.response;

import com.port.portcoin.domain.portfolio.entity.Portfolio;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PortfolioResponse {
    private final UUID id;
    private final String name;

    public PortfolioResponse(Portfolio portfolio){
        this.id = portfolio.getUser().getId();
        this.name = portfolio.getName();
    }
}
