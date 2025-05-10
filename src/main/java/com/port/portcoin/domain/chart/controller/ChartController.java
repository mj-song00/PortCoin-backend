package com.port.portcoin.domain.chart.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.chart.dto.request.CoinChartRequest;
import com.port.portcoin.domain.chart.dto.response.CoinChartResponse;
import com.port.portcoin.domain.chart.service.ChartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.port.portcoin.domain.user.dto.AuthUser;

import java.util.List;

@Tag(name = "Chart", description = "개별 coin들의 chart 관련 API입니다.")
@RestController
@RequestMapping("/api/v1/chart")
@RequiredArgsConstructor
@Slf4j
public class ChartController {

    private final ChartService chartService;
    /**
     * CoinGecKo에서 제공하는 API를  이용합니다.
     * API 재사용 대기시간은 1분 입니다.
     */
    @Operation(summary = "개별코인 시세조회", description = "코인별 시세를 조회합니다.")
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<CoinChartResponse>> getChart(
            @RequestBody CoinChartRequest coinChartRequest,
            @Auth AuthUser authUser
    ){
       CoinChartResponse result = chartService.getChart(coinChartRequest, authUser);
        ApiResponse<CoinChartResponse> response = ApiResponse.successWithData(result,ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
