package com.port.portcoin.domain.portfoliocoin.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.coin.entity.Coin;
import com.port.portcoin.domain.coin.repository.CoinRepository;
import com.port.portcoin.domain.portfolio.entity.Portfolio;
import com.port.portcoin.domain.portfolio.repository.PortfolioRepository;
import com.port.portcoin.domain.portfoliocoin.dto.request.CoinAdd;
import com.port.portcoin.domain.portfoliocoin.dto.request.CoinEditRequest;
import com.port.portcoin.domain.portfoliocoin.dto.request.CoinUpdate;
import com.port.portcoin.domain.portfoliocoin.dto.response.CoinProfitLossResponse;
import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import com.port.portcoin.domain.portfoliocoin.repository.PortfolioCoinRepository;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PortfolioCoinServiceImpl implements PortfolioCoinService {

    private final PortfolioRepository portfolioRepository;
    private final CoinRepository coinRepository;
    private final PortfolioCoinRepository portfolioCoinRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, List<CoinMarketResponse>> redisTemplate;

    @Transactional
    public void editPortfolioCoins(CoinEditRequest coinEditRequest, AuthUser authUser) {
        Portfolio portfolio = portfolioRepository.findByPortfolioId(coinEditRequest.getPortfolioId())
                .orElseThrow(() -> new BaseException(ExceptionEnum.PORTFOLIO_NOT_FOUND));

        if (!portfolio.getUser().getId().equals(authUser.getId())) {
            throw new BaseException(ExceptionEnum.USER_NOT_FOUND);
        }

        // 삭제
        if (coinEditRequest.getToDelete() != null) {
            for (Long portfolioCoinId : coinEditRequest.getToDelete()) {
                portfolioCoinRepository.deleteById(portfolioCoinId);
            }
        }

        // 수정
        if (coinEditRequest.getToUpdate() != null) {
            for (CoinUpdate update : coinEditRequest.getToUpdate()) {
                PortfolioCoin coin = portfolioCoinRepository.findById(update.getPortfolioCoinId())
                        .orElseThrow(() -> new BaseException(ExceptionEnum.PORTFOLIO_COIN_NOT_FOUND));
                coin.update(update.getAmount(), update.getPurchasePrice());
            }
        }

        // 추가
        if (coinEditRequest.getToAdd() != null) {
            for (CoinAdd add : coinEditRequest.getToAdd()) {
                Coin coin = coinRepository.findById(add.getCoinId())
                        .orElseThrow(() -> new BaseException(ExceptionEnum.COIN_NOT_FOUND));
                PortfolioCoin portfolioCoin = new PortfolioCoin(portfolio, coin, add.getAmount(), add.getPurchasePrice());
                portfolioCoinRepository.save(portfolioCoin);
            }
        }
    }

    @Override
    public List<CoinProfitLossResponse> getResult(AuthUser authUser) {
        User user = getUser(authUser.getId());

        if(!user.getId().equals(authUser.getId())) throw new BaseException(ExceptionEnum.USER_NOT_FOUND);

        List<PortfolioCoin> userCoins = portfolioCoinRepository.findByPortfolioUserId(authUser.getId());
        List<CoinProfitLossResponse> results = new ArrayList<>();

        for (PortfolioCoin coin : userCoins) {
            System.out.println((coin.getCoin().getSymbol().toLowerCase()));
            double currentPrice =  getCurrentPrice(coin.getCoin().getSymbol().toLowerCase());
            double purchasePrice = coin.getPurchasePrice();

            double profitLoss = (currentPrice - purchasePrice) / purchasePrice * 100; // 퍼센트 계산

            // Redis에서 CoinMarketResponse 목록 가져오기
            String imageKey = "CoinGecko:coin:image:" + coin.getCoin().getSymbol().toLowerCase();
            List<CoinMarketResponse> coinMarketResponses = redisTemplate.opsForValue().get(imageKey); // Redis에서 이미지 및 가격 데이터 가져오기

            String coinImage = getCoinImage(coin.getCoin().getSymbol());
            if (coinMarketResponses != null && !coinMarketResponses.isEmpty()) {
                // CoinMarketResponse에서 이미지 정보 추출
                for (CoinMarketResponse response : coinMarketResponses) {
                    if (response.getSymbol().equalsIgnoreCase(coin.getCoin().getSymbol())) {
                        coinImage = response.getImage(); // 해당 코인의 이미지를 찾았다면 그 값을 설정
                        break;
                    }
                }
            }

            CoinProfitLossResponse dto = new CoinProfitLossResponse(
                    coin.getCoin().getName(),
                    coinImage, // Redis에서 가져온 이미지
                    profitLoss
            );

            results.add(dto);
        }
        return results;
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    // CoinGecko에서 코인 현재 가격을 가져오는 메서드
    private double getCurrentPrice(String coinId) {
        // CoinGecko API를 호출하여 현재 가격 가져오기
        List<CoinMarketResponse> allCoins = redisTemplate.opsForValue().get("all_coins");

        if (allCoins == null) {
            throw new RuntimeException("코인 데이터가 없습니다.");
        }

        // coinId에 해당하는 코인 찾아서 현재 가격 반환
        return allCoins.stream()
                .filter(coin -> coin.getSymbol().equals(coinId))
                .findFirst()
                .map(CoinMarketResponse::getCurrentPrice)
                .orElseThrow(() -> new RuntimeException("해당 코인을 찾을 수 없습니다: " + coinId));

    }

    private String getCoinImage(String coinSymbol) {
        List<CoinMarketResponse> allCoins = redisTemplate.opsForValue().get("all_coins");

        if (allCoins == null) {
            throw new RuntimeException("코인 데이터가 없습니다.");
        }

        return allCoins.stream()
                .filter(coin -> coin.getSymbol().equalsIgnoreCase(coinSymbol))
                .findFirst()
                .map(CoinMarketResponse::getImage)
                .orElse("default-image-url");
    }
}