package com.port.portcoin.domain.coin.controller;

import com.port.portcoin.common.annotation.Auth;
import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.external.coingecko.CoinGecko;
import com.port.portcoin.domain.coin.dto.request.CreateCoinRequest;
import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.coin.service.CoinService;
import com.port.portcoin.domain.user.dto.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Tag(name = "Coin", description = "코인 관련 API, ADMIN만 등록이 가능합니다.")
@RestController
@RequestMapping("/api/v1/coin")
@RequiredArgsConstructor
@Slf4j
public class CoinController {

    private final CoinService coinService;
    private final CoinGecko coinGecko;
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;

    @Operation(summary = "코인 등록", description = "코인을 등록합니다.")
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> createCoin(
            @Valid @RequestBody CreateCoinRequest coinRequest,
            @Auth AuthUser authUser
    ) {
        coinService.registration(coinRequest, authUser);
        ApiResponse<Void> response =
                ApiResponse.successWithOutData(ApiResponseEnum.REGISTRATION_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * CoinGecko에서 제공하는 API를 사용하는 controller 입니다.
     *
     *  CoinGecko 코인 조회 API
     *  API 재사용 대기시간은 1분 입니다.
     */

    @Operation(summary = "시장 코인 조회", description = "코인들의 시장값을 조회합니다")
    @GetMapping("/price")
    public ResponseEntity<ApiResponse<List<CoinMarketResponse>>> getTopCoins(){
        // Redis에서 전체 코인 데이터를 조회
        List<CoinMarketResponse> coinList = redisTemplate.opsForValue().get("all_coins");

        if (coinList == null) {
            // Redis에 데이터가 없으면 CoinGecko API 호출 후 Redis에 저장
            coinList = coinGecko.getCoinList();
        }

        // 상위 10개 코인만 추출
        List<CoinMarketResponse> topCoins = coinList.stream()
                .limit(10)
                .collect(Collectors.toList());
        ApiResponse<List<CoinMarketResponse>> response = ApiResponse.successWithData(topCoins,ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
