package com.port.portcoin.domain.user.validation;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.port.portcoin.common.exception.ExceptionEnum;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserValidation {
    private final UserRepository userRepository;

    // 인증된 사용자 확인
    public void validateAuthenticatedUser(AuthUser authUser) {
        if (authUser == null) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_USER);
        }
    }

    // id로 사용자 조회
    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    // 사용자 탈퇴 여부 확인
    public void validateUserNotDeleted(User user) {
        if (user.getDeletedAt() != null) {
            throw new BaseException(ExceptionEnum.ALREADY_DELETED);
        }
    }
}
