package com.port.portcoin.domain.analysis.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.analysis.dto.response.UserSummeryResponse;
import com.port.portcoin.domain.analysis.service.AnalysisService;
import com.port.portcoin.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Analysis", description = "통계관련 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/analysis")
@Slf4j
public class analysisController {

    private final AnalysisService ananlysisService;

    /***************************/
    // 총 가입 유저수 확인 api
    /***************************/
    @Operation(summary = "전체 가입자수 확인")
    @GetMapping("/total")
    public ResponseEntity<Long> getUsers(
            @Auth AuthUser authUser
    ) {
        Long result = ananlysisService.getTotalUser(authUser);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /***************************/
    // 3,6,9,12개월 가입 유저수 확인 api
    /***************************/
    @Operation(summary = "3,6,9,12개월 가입 유저수 확인")
    @GetMapping("/signup-summery")
    public ResponseEntity<ApiResponse<List<UserSummeryResponse>>> getResult(
            @Auth AuthUser authUser
    ) {
        List<UserSummeryResponse> result = ananlysisService.getSummeryResult(authUser);
        ApiResponse<List<UserSummeryResponse>> response = ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(response);
    }
}
