package com.port.portcoin.domain.analysis.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;
import com.port.portcoin.domain.analysis.dto.response.HoldingDistributionResponse;
import com.port.portcoin.domain.analysis.repository.CoinAnalysisCustomRepository;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.enums.UserRole;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CoinAnalysisService {

    private final UserRepository userRepository;
    private final CoinAnalysisCustomRepository coinAnalysisRepository;


    public List<CoinDataResponse> getSummeryResult(AuthUser authUser) {
        validateAdminUser(authUser.getId());

        return coinAnalysisRepository.findByCoinId();
    }

    public List<HoldingDistributionResponse> getUserDistribution(AuthUser authUser) {
        validateAdminUser(authUser.getId());

        return coinAnalysisRepository.findByUserDistribution();
    }

    public Double getReturn(AuthUser authUser) {
        validateAdminUser(authUser.getId());

        return coinAnalysisRepository.calculateRateOfReturn();
    }


    private void validateAdminUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
        }
    }
}
