package com.port.portcoin.domain.analysis.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.analysis.dto.response.UserSummeryResponse;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.enums.UserRole;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final UserRepository userRepository;

    public Long getTotalUser(AuthUser authUser) {
        User user = getUser(authUser.getId());
        validateUserExists(user.getId());

        return userRepository.count();
    }

    public UserSummeryResponse getSummeryResult(AuthUser authUser) {
        User user = getUser(authUser.getId());
        validateUserExists(user.getId());

        UserSummeryResponse result =
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    private void validateUserExists(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new BaseException(ExceptionEnum.USER_NOT_FOUND);
        }
    }


}
