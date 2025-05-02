package com.port.portcoin.domain.admin.service;

import com.port.portcoin.common.config.PasswordEncoder;
import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.common.repository.RefreshTokenRepository;
import com.port.portcoin.domain.user.dto.request.LoginRequest;
import com.port.portcoin.domain.user.dto.request.SignupRequest;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.enums.UserRole;
import com.port.portcoin.domain.user.repository.UserRepository;
import com.port.portcoin.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void createAdminUser(SignupRequest signupRequest) {
        Optional<User> userByEmail = userRepository.findByEmail(signupRequest.getEmail());
        Optional<User> userByNickname = userRepository. findByNickName(signupRequest.getNickName());

        if (userByEmail.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }

        if (userByNickname.isPresent()) {
            throw new BaseException(ExceptionEnum.USER_ALREADY_EXISTS);
        }
        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.ADMIN;
        User user = new User(
                signupRequest.getEmail(),
                signupRequest.getNickName(),
                encodedPassword,
                userRole
        );

        userRepository.save(user);
    }

    // 리프레시 토큰 생성
    public String generateRefreshToken(String email) {
        User user = findByEmail(email);
        return jwtUtil.createRefreshToken(user.getId());
    }

    @Override
    public void saveRefreshToken(String email, String refreshToken) {
        String key = "refresh:" + email;
        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
    }

    @Override
    public void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge((long) 10 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @Override
    @Transactional
    public String refreshAccessToken(String refreshToken, HttpServletResponse response) {
        User user = validateRefreshToken(refreshToken);
        long expiration = jwtUtil.getRemainingExpiration(refreshToken);

        try {
            refreshTokenRepository.addBlacklist(refreshToken, expiration);

            String newRefreshToken = generateRefreshToken(user.getEmail());
            saveRefreshToken(user.getEmail(), newRefreshToken);
            setRefreshTokenCookie(response, newRefreshToken);
        } catch (Exception e) {
            log.error("Redis 처리 실패", e);
        }

        return generateAccessToken(user);
    }

    @Override
    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        User user = validateRefreshToken(refreshToken);

        try {
            //Redis에서 refreshToken 삭제
            redisTemplate.delete("refresh:" + user.getEmail());
        } catch (Exception e) {
            log.error("Redis 처리 실패", e);
        }

        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "") // 빈 문자열 사용
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }


    @Override
    public String login(LoginRequest loginRequest) {
        User user = findByEmail(loginRequest.getEmail());
        validateUserNotDeleted(user);
        authenticateUser(user, loginRequest.getPassword());
        return generateAccessToken(user);
    }


    // 이메일로 사용자 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    // 사용자 탈퇴 여부 확인
    public void validateUserNotDeleted(User user) {
        if (user.getDeletedAt() != null) {
            throw new BaseException(ExceptionEnum.ALREADY_DELETED);
        }
    }

    // 비밀번호 인증
    public void authenticateUser(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BaseException(ExceptionEnum.EMAIL_PASSWORD_MISMATCH);
        }
    }

    // 액세스 토큰 생성
    private String generateAccessToken(User user) {
        return jwtUtil.createToken(user.getId(), user.getUserRole());
    }

    private User validateRefreshToken(String refreshToken) {
        if (refreshToken == null || refreshTokenRepository.isBlacklisted(refreshToken)
                || !jwtUtil.isTokenValid(refreshToken)) {
            throw new BaseException(ExceptionEnum.INVALID_REFRESH_TOKEN);
        }

        Claims claims = jwtUtil.extractClaims(refreshToken);
        UUID userId = UUID.fromString(claims.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        String storedToken = refreshTokenRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new BaseException(ExceptionEnum.INVALID_REFRESH_TOKEN));

        if (!storedToken.equals(refreshToken)) {
            throw new BaseException(ExceptionEnum.INVALID_REFRESH_TOKEN);
        }

        return user;
    }
}
