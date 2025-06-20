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
                PortfolioCoin portfolioCoin = new PortfolioCoin(portfolio, coin, add.getAmount(), add.getPurchasePrice(), add.getCurrentPrice(), add.getPurchaseDate());
                portfolioCoinRepository.save(portfolioCoin);
            }
        }
    }

    @Override
    public List<CoinProfitLossResponse> getResult(AuthUser authUser) {
        User user = getUser(authUser.getId());

        if(!user.getId().equals(authUser.getId())) throw new BaseException(ExceptionEnum.USER_NOT_FOUND);

        List<PortfolioCoin> userCoins = portfolioCoinRepository.findByPortfolioUserIdAndPortfolioDeletedAtIsNull(authUser.getId());
        List<CoinProfitLossResponse> results = new ArrayList<>();

        for (PortfolioCoin coin : userCoins) {
            Double amount = coin.getAmount();
            System.out.println(amount);
            if (amount == null || amount <= 0) {
                continue; // 수량 0인 경우는 제외
            }
            String symbol = coin.getCoin().getSymbol().toLowerCase();
            double currentPrice = getCurrentPrice(symbol);          // 현재 시세 (단가)
            double purchasePrice = coin.getPurchasePrice();         // 총 매수 금액 (amount * 매입 단가)

            // 평가 금액 = 현재 단가 * 수량
            double currentValue = currentPrice * amount;

            // 수익률 = (평가금액 - 총매수금액) / 총매수금액 * 100
            double profitLoss = Math.round(
                    ((currentValue - purchasePrice) / purchasePrice * 100) * 100.0
            ) / 100.0;

            String coinImage = getCoinImage(symbol);

            results.add(new CoinProfitLossResponse(
                    coin.getCoin().getName(),
                    coinImage,
                    profitLoss
            ));
        }
        return results;
    }

    private User getUser(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
    }

    // CoinGecko에서 코인 현재 가격을 가져오는 메서드
    private double getCurrentPrice(String coinId) {
        List<CoinMarketResponse> allCoins = redisTemplate.opsForValue().get("CoinGeckoMarket:top10");

        if (allCoins == null) {
            throw new BaseException(ExceptionEnum.COIN_NOT_FOUND);
        }

        return allCoins.stream()
                .filter(coin -> coin.getSymbol().equalsIgnoreCase(coinId))
                .findFirst()
                .map(CoinMarketResponse::getCurrentPrice)
                .orElseThrow(() -> new BaseException(ExceptionEnum.COIN_NOT_FOUND));
    }


    private String getCoinImage(String coinSymbol) {
        List<CoinMarketResponse> allCoins = redisTemplate.opsForValue().get("CoinGeckoMarket:top10");

        if (allCoins == null) {
            throw new BaseException(ExceptionEnum.COIN_NOT_FOUND);
        }

        return allCoins.stream()
                .filter(coin -> coin.getSymbol().equalsIgnoreCase(coinSymbol))
                .findFirst()
                .map(CoinMarketResponse::getImage)
                .orElse("default-image-url");
    }
}