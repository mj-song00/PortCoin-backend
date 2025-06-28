package com.port.portcoin.domain.external.coingecko.update;

import com.port.portcoin.domain.coin.dto.response.CoinMarketResponse;
import com.port.portcoin.domain.portfoliocoin.entity.PortfolioCoin;
import com.port.portcoin.domain.portfoliocoin.repository.PortfolioCoinRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CoinPriceUpdate {
    private final PortfolioCoinRepository portfolioCoinRepository;

    public CoinPriceUpdate(PortfolioCoinRepository portfolioCoinRepository) {
        this.portfolioCoinRepository = portfolioCoinRepository;
    }

    @Transactional
    public void updatePortfolioCoinPrices(List<CoinMarketResponse> marketData) {
        List<PortfolioCoin> portfolioCoins = portfolioCoinRepository.findAllWithCoin();

        for (PortfolioCoin pc : portfolioCoins) {
            String symbol = pc.getCoin().getSymbol();
            marketData.stream()
                    .filter(c -> c.getSymbol().equalsIgnoreCase(symbol))
                    .findFirst()
                    .ifPresent(matching -> pc.updateCurrentPrice(matching.getCurrentPrice()));
        }

        portfolioCoinRepository.saveAll(portfolioCoins);
    }
}
