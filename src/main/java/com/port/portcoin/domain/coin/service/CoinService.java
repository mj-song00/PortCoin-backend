package com.port.portcoin.domain.coin.service;

import com.port.portcoin.domain.coin.dto.request.CreateCoinRequest;
import com.port.portcoin.domain.coin.dto.response.CoinListResponse;
import com.port.portcoin.domain.user.dto.AuthUser;
import jakarta.validation.Valid;

import java.util.List;

public interface CoinService {

    void registration(@Valid CreateCoinRequest coinRequest, AuthUser authUser);

    List<CoinListResponse> getCoinList();
}
