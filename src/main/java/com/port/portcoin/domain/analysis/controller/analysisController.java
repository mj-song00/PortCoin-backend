package com.port.portcoin.domain.analysis.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.analysis.dto.response.DateCountResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    @GetMapping("/users/total")
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
    @GetMapping("/users/signup-summery")
    public ResponseEntity<ApiResponse<List<UserSummeryResponse>>> getResult(
            @Auth AuthUser authUser
    ) {
        List<UserSummeryResponse> result = ananlysisService.getSummeryResult(authUser);
        ApiResponse<List<UserSummeryResponse>> response = ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(response);
    }

    /***************************/
    // 오늘 기준 -n주 전부터 가입자 조회
    // 예) 오늘 기준 12주전 부터 가입한 유저 조회
    /***************************/
    @Operation(summary = "오늘 기준 -n주 전부터 가입자 조회 " , description = "오늘 기준 n주전 부터 가입한 유저를 1주일 단위로 조회합니다." )
    @GetMapping("/users/weekly")
    public ResponseEntity<ApiResponse<List<DateCountResponse>>> getResult(
            @Auth AuthUser authUser,
            @RequestParam String week
    ){
        int weeksAgo;
        try {
            weeksAgo = Integer.parseInt(week);
        } catch (NumberFormatException e) {
            throw new  BaseException(ExceptionEnum.TYPE_ERROR);
        }


        List<DateCountResponse> result = ananlysisService.getWeeklyResult(authUser, weeksAgo);
        ApiResponse<List<DateCountResponse>> response =  ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(response);
    }

    /***************************/
    // 월별 가입자 수 조회
    /***************************/
    @Operation(summary = "월 별 가입자 조회 " , description = "1년간 월별 가입자를 조회합니다." )
    @GetMapping("/users/monthly")
    public ResponseEntity<ApiResponse<List<DateCountResponse>>> getMonthly(
            @Auth AuthUser authUser
    ){
        List<DateCountResponse> result = ananlysisService.getMonthlyResult(authUser);
        ApiResponse<List<DateCountResponse>> response =  ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(response);
    }


}
