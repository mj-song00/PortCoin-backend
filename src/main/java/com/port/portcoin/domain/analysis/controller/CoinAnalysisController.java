package com.port.portcoin.domain.analysis.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;
import com.port.portcoin.domain.analysis.service.CoinAnalysisService;
import com.port.portcoin.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
