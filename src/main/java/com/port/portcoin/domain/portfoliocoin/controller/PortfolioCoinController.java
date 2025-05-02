package com.port.portcoin.domain.portfoliocoin.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.portfoliocoin.dto.request.CoinEditRequest;
import com.port.portcoin.domain.portfoliocoin.dto.response.CoinProfitLossResponse;
import com.port.portcoin.domain.portfoliocoin.service.PortfolioCoinService;
import com.port.portcoin.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "PortfolioCoinRegistration", description = "포트폴리오 코인 관련 API")
@RestController
@RequestMapping("/api/v1/portfolio-coins")
@RequiredArgsConstructor
@Slf4j
public class PortfolioCoinController {

    private final PortfolioCoinService portfolioCoinService;

    @Operation(summary = "포트폴리오 코인 등록", description = "포트폴리오에 코인을 일괄 등록/수정/삭제합니다.")
    @PatchMapping("/edit")
    public ResponseEntity<ApiResponse<Void>> editPortfolioCoins(
            @Valid @RequestBody CoinEditRequest coinRegisterRequest,
            @Auth AuthUser authUser
            ){
        portfolioCoinService.editPortfolioCoins(coinRegisterRequest, authUser);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.REGISTRATION_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "포트폴리오 코인별 가치 조회", description = "현재 시세를 기반으로 사용자들의 코인 가치를 조회합니다.")
    @GetMapping("/value")
    public ResponseEntity<List<CoinProfitLossResponse>> getResult(
            @Auth AuthUser authUser
    ){
        List<CoinProfitLossResponse> response = portfolioCoinService.getResult(authUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
