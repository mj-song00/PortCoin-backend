package com.port.portcoin.domain.analysis.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.analysis.dto.response.DateCountResponse;
import com.port.portcoin.domain.analysis.dto.response.UserSummeryResponse;
import com.port.portcoin.domain.analysis.repository.AnalysisCustomRepository;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.enums.UserRole;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisService {

    private final AnalysisCustomRepository analysisRepository;
    private final UserRepository userRepository;

    public Long getTotalUser(AuthUser authUser) {
        validateAdminUser(authUser.getId());
        return userRepository.count();
    }

    public List<UserSummeryResponse> getSummeryResult(AuthUser authUser) {
        validateAdminUser(authUser.getId());
        return analysisRepository.findByNMonth();

    }

    public List<DateCountResponse> getWeeklyResult(AuthUser authUser,  int weeksAgo) {
        validateAdminUser(authUser.getId());
        LocalDate startDate = LocalDate.now().minusWeeks(weeksAgo);
        return analysisRepository.findByNWeek(startDate);
    }

    public List<DateCountResponse> getMonthlyResult(AuthUser authUser) {
        validateAdminUser(authUser.getId());

        return analysisRepository.findByMonth();
    }

    private void validateAdminUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
        }
    }

}
