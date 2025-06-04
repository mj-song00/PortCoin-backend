package com.port.portcoin.domain.analysis.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.analysis.dto.response.*;
import com.port.portcoin.domain.analysis.service.AssetService;
import com.port.portcoin.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Asset-Analysis", description = "자산에 대한 통계 API 입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/analysis/asset")
@Slf4j
public class AssetController {
    private final AssetService assetService;

    /***************************/
    // 코인별 자산 합계 API (원형)
    /***************************/
    @Operation(summary = "코인별 자산 원형 그래프 조회", description = "상위 10개의 코인들의 개별 합계를 제공합니다.")
    @GetMapping("/summery")
    public ResponseEntity<ApiResponse<List<PieChartResponse>>> getPieChart(
            @Auth AuthUser authUser
    ){
        List<PieChartResponse> result = assetService.getPieChart(authUser);
        ApiResponse<List<PieChartResponse>> response = ApiResponse.successWithData(result, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(response);
    }

    /***************************/
    // 코인별 자산 합계 API (테이블)
    /***************************/
    @Operation(summary = "코인별 자산 테이블 조회", description = "코인들의 개별 합계와 평균을 조회합니다.")
    @GetMapping("/table")
    public ResponseEntity<ApiResponse<AssetTablePageResponse>> getTable(
            @Auth AuthUser authUser,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<AssetTableResponseItem> summeryCoins = assetService.getSummery(pageable, authUser);
        AssetTablePageResponse   response = new AssetTablePageResponse (
                summeryCoins.getContent(),
                summeryCoins.getNumber(),
                summeryCoins.getTotalPages(),
                summeryCoins.getTotalElements()
        );

        ApiResponse<AssetTablePageResponse> result = ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(result);
    }

    /***************************/
    // 유저 평균 자산
    /***************************/
    @Operation(summary = "유저 평균 자산", description = "유저들의 평균 자산을 조회합니다.")
    @GetMapping("/average")
    public ResponseEntity<ApiResponse<TotalAssetResponse>> getAverage(
            @Auth AuthUser authUser
    ){
        TotalAssetResponse response = assetService.getAverage(authUser);
        ApiResponse<TotalAssetResponse> result = ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(result);
    }

    /***************************/
    // 자산 평균 하위 10%
    /***************************/
    @Operation(summary = "유저 자산 평균 하위 10% 자산 조회", description = "유저 자산 평균 하위 10% 유저들의 자산을 조회합니다.")
    @GetMapping("/bottom")
    public ResponseEntity<ApiResponse<List<UserAssetSummary>>> getBottom(
            @Auth AuthUser authUser
    ){
        List<UserAssetSummary> response = assetService.getBottom(authUser);
        ApiResponse<List<UserAssetSummary>> result = ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(result);
    }

    /***************************/
    // 자산 평균 상위 10%
    /***************************/
    @Operation(summary = "유저 자산 평균 상위 10% 자산 조회", description = "유저 자산 평균 상위 10% 유저들의 자산을 조회합니다.")
    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<UserAssetSummary>>> getTop(
            @Auth AuthUser authUser
    ){
        List<UserAssetSummary> response = assetService.getTop(authUser);
        ApiResponse<List<UserAssetSummary>> result = ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.ok(result);
    }
}

