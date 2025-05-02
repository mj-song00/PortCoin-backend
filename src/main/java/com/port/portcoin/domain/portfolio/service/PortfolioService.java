package com.port.portcoin.domain.portfolio.service;

import com.port.portcoin.domain.portfolio.dto.request.PortfolioRequest;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioDetailResponse;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioResponse;
import com.port.portcoin.domain.user.dto.AuthUser;
import jakarta.validation.Valid;

import java.util.ArrayList;

public interface PortfolioService {
     void createPortfolio(@Valid PortfolioRequest portfolioRequest, AuthUser authUser);

     void updatePortfolioName(@Valid PortfolioRequest portfolioRequest, AuthUser authUser,  Long portfolioId);

     void deletePortfolio(AuthUser authUser,  Long portfolioId);

     ArrayList<PortfolioResponse> getPortfolios(AuthUser authUser);

     PortfolioDetailResponse getDetail(Long portfolioId, AuthUser authUser);
}
