package com.port.portcoin.domain.portfolio.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.portfolio.dto.request.PortfolioRequest;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioDetailResponse;
import com.port.portcoin.domain.portfolio.dto.response.PortfolioResponse;
import com.port.portcoin.domain.portfolio.service.PortfolioService;
import com.port.portcoin.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Tag(name = "Portfolio", description = "포트폴리오 관련 API")
@RestController
@RequestMapping("/api/v1/portfolio")
@RequiredArgsConstructor
@Slf4j
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "포트폴리오 생성", description = "포트폴리오를 생성합니다.")
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createPortfolio(
            @Valid @RequestBody PortfolioRequest portfolioRequest,
            @Auth AuthUser authUser
    ) {
        portfolioService.createPortfolio(portfolioRequest, authUser);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.CREATE_PORTFOLIO_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //수정
    @Operation(summary = "포트폴리오 수정", description = "포트폴리오를 수정합니다.")
    @PatchMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<Void>> updatePortfolio(
            @Valid @RequestBody PortfolioRequest portfolioRequest,
            @Auth AuthUser authUser,
            @PathVariable Long portfolioId
    ) {
        portfolioService.updatePortfolioName(portfolioRequest, authUser, portfolioId);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.CREATE_PORTFOLIO_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //삭제
    @Operation(summary = "포트폴리오 삭제", description = "포트폴리오를 삭제합니다.")
    @PatchMapping("/{portfolioId}/delete")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            @Auth AuthUser authUser,
            @PathVariable Long portfolioId
    ){
        portfolioService.deletePortfolio(authUser, portfolioId);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.DELETE_PORTFOLIO_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //일괄 조회
    @Operation(summary = "포트폴리오 조회", description = "포트폴리오를 일괄 조회합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<ArrayList<PortfolioResponse>>>  check(
            @Auth AuthUser authUser
    ){
        ArrayList<PortfolioResponse> lists = portfolioService.getPortfolios(authUser);
        ApiResponse<ArrayList<PortfolioResponse>> response = ApiResponse.successWithData(lists, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //개별 조회
    @Operation(summary = "포트폴리오 개별 조회", description = "개별 포트폴리오를 조회합니다.")
    @GetMapping("/{portfolioId}")
    public ResponseEntity<ApiResponse<PortfolioDetailResponse>> getDetailPortfolio(
            @PathVariable Long portfolioId,
             @Auth AuthUser authUser
    ){
        PortfolioDetailResponse response = portfolioService.getDetail(portfolioId, authUser);
        return ResponseEntity.ok(ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS));
    }
}
