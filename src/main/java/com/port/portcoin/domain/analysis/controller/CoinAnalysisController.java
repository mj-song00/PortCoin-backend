package com.port.portcoin.domain.analysis.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;
import com.port.portcoin.domain.analysis.dto.response.HoldingDistributionResponse;
import com.port.portcoin.domain.analysis.dto.response.UserProfitResponse;
import com.port.portcoin.domain.analysis.dto.response.UserProfitResponseItem;
import com.port.portcoin.domain.analysis.service.CoinAnalysisService;
import com.port.portcoin.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Coin-Analysis", description = "coin과 관련된 통계 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/analysis/coin")
@Slf4j
public class CoinAnalysisController {

    private final CoinAnalysisService coinAnalysisService;

    /***************************/
    // 전체 코인 보유량 조회 api
    /***************************/
    @Operation(summary = "전체 코인 보유량 조회", description = "전체 유저가 가진 코인별 합계를 조회합니다.")
    @GetMapping("/summery")
    public ResponseEntity<ApiResponse<List<CoinDataResponse>>> getResult(
            @Auth AuthUser authUser
    ){
        List<CoinDataResponse> result = coinAnalysisService.getSummeryResult(authUser);
        ApiResponse<List<CoinDataResponse>> response = ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(response);

    }

    /***************************/
    // 코인별 유저 분포 조회 api
    /***************************/
    @Operation(summary = "코인별 유저 분포 조회", description = "코인별 유저 분포를 조회합니다.")
    @GetMapping("/holding-distribution")
    public ResponseEntity<ApiResponse<List<HoldingDistributionResponse>>> getUserDistribution(
            @Auth AuthUser authUser
    ){
        List<HoldingDistributionResponse> result = coinAnalysisService.getUserDistribution(authUser);
        ApiResponse<List<HoldingDistributionResponse>> response = ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(response);
    }

    /***************************/
    // 전체 유저 수익률 api
    /***************************/
    @Operation(summary = "전체 유저 수익률 조회", description = "전체 유저들의 수익률을 조회합니다.")
    @GetMapping("/return")
    public ResponseEntity<Double>getRateOfReturn(
            @Auth AuthUser authUser
    ){
        Double result = coinAnalysisService.getReturn(authUser);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /***************************/
    // 상위 유저 수익률 api
    /***************************/
    @Operation(summary = "상위 5% 유저 수익률 조회", description = "상위 5% 유저의 수익률을 조회합니다.")
    @GetMapping("/top-ranking")
    public ResponseEntity<ApiResponse<UserProfitResponse>> getTopRank(
            @Auth AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserProfitResponseItem> top5List = coinAnalysisService.getTopRank(pageable, authUser);
        return buildPagedResponse(top5List);
    }

    /***************************/
    // 하위 유저 수익률 api
    /***************************/
    @Operation(summary = "하위 10% 유저 수익률 조회", description = "하위 10% 유저의 수익률을 조회합니다.")
    @GetMapping("/bottom-ranking")
    public ResponseEntity<ApiResponse<UserProfitResponse>> getBottomRank(
            @Auth AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<UserProfitResponseItem> topBottomList = coinAnalysisService.getBottomRank(pageable, authUser);
        return buildPagedResponse(topBottomList );
    }

    private ResponseEntity<ApiResponse<UserProfitResponse>> buildPagedResponse(Page<UserProfitResponseItem> pageData) {
        UserProfitResponse response = new UserProfitResponse(
                pageData.getContent(),
                pageData.getNumber(),
                pageData.getTotalPages(),
                pageData.getTotalElements()
        );
        ApiResponse<UserProfitResponse> apiResponse = ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(apiResponse);
    }
}
