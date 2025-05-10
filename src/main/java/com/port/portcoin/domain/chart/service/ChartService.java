package com.port.portcoin.domain.chart.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.chart.dto.request.CoinChartRequest;
import com.port.portcoin.domain.chart.dto.response.CoinChartResponse;
import com.port.portcoin.domain.external.coingecko.CoinGecko;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChartService {

    private final CoinGecko coinGecko;
    private final UserRepository userRepository;


    public CoinChartResponse getChart(CoinChartRequest coinChartRequest, AuthUser authUser) {
       User user = getUser(authUser.getId());

       if (!user.getId().equals(authUser.getId())) throw new BaseException(ExceptionEnum.USER_NOT_FOUND);

       return coinGecko.getCoinChart(coinChartRequest.getSymbol(), coinChartRequest.getDays());
    }


    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }
}
