package com.port.portcoin.domain.admin.service;

import com.port.portcoin.domain.user.dto.request.LoginRequest;
import com.port.portcoin.domain.user.dto.request.SignupRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public interface AdminService {
    void createAdminUser(@Valid SignupRequest signupRequest);

    String login(@Valid LoginRequest loginRequest);

    String generateRefreshToken(@Email(message = "이메일 형식이 올바르지 않습니다.") @NotBlank(message = "이메일을 입력해주세요.") String email);

    void saveRefreshToken(@Email(message = "이메일 형식이 올바르지 않습니다.") @NotBlank(message = "이메일을 입력해주세요.") String email, String refreshToken);

    void setRefreshTokenCookie(HttpServletResponse response, String refreshToken);

    String refreshAccessToken(String refreshToken, HttpServletResponse response);

    void logout(String refreshToken, HttpServletResponse response);
}
