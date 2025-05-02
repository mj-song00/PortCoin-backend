package com.port.portcoin.domain.coin.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.coin.dto.request.CreateCoinRequest;
import com.port.portcoin.domain.coin.entity.Coin;
import com.port.portcoin.domain.coin.repository.CoinRepository;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;

    @Override
    public void registration(CreateCoinRequest coinRequest, AuthUser authUser){
        if (!authUser.getRole().equals(UserRole.ADMIN)) throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);

        Coin coin = new Coin(coinRequest.getSymbol(), coinRequest.getName());
        coinRepository.save(coin);
    }
}
