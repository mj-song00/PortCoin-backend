package com.port.portcoin.domain.portfoliocoin.service;

import com.port.portcoin.domain.portfoliocoin.dto.request.CoinEditRequest;
import com.port.portcoin.domain.portfoliocoin.dto.response.CoinProfitLossResponse;
import com.port.portcoin.domain.user.dto.AuthUser;
import jakarta.validation.Valid;

import java.util.List;

public interface PortfolioCoinService {

    void editPortfolioCoins(@Valid CoinEditRequest coinRegisterRequest, AuthUser authUser);

   List<CoinProfitLossResponse> getResult(AuthUser authUser);
}
