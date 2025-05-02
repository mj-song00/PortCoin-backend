package com.port.portcoin.domain.admin.controller;

import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.admin.service.AdminService;
import com.port.portcoin.domain.user.dto.request.LoginRequest;
import com.port.portcoin.domain.user.dto.request.SignupRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Admin 관련 API")
@RestController
@RequestMapping("/api/v2/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "admin 계정 회원가입", description = "admin 계정의 회원가입을 진행합니다.")
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<Void>> signup(
            @Valid @RequestBody SignupRequest signupRequest) {
        adminService.createAdminUser(signupRequest);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.SIGNUP_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그인", description = "admin 사용자의 로그인을 진행합니다.")
    @PostMapping("/sign-in")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        // 로그인 후 토큰 발급
        String accessToken = adminService.login(loginRequest);
        String refreshToken = adminService.generateRefreshToken(loginRequest.getEmail());

        // 액세스 토큰 redis 저장
        adminService.saveRefreshToken(loginRequest.getEmail(), refreshToken);

        // 리프레시 토큰을 HTTP-Only 쿠키로 설정
        adminService.setRefreshTokenCookie(response, refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(accessToken);
    }

    // 리프레시 토큰으로 액세스 토큰 재발급
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        String newAccessToken = adminService.refreshAccessToken(refreshToken, response);
        return ResponseEntity.ok(newAccessToken);
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "사용자가 로그아웃합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        adminService.logout(refreshToken, response);
        return ResponseEntity.ok().build();
    }
}
