package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.port.portcoin.domain.coin.entity.QCoin.coin;
import static com.port.portcoin.domain.portfolio.entity.QPortfolio.portfolio;
import static com.port.portcoin.domain.portfoliocoin.entity.QPortfolioCoin.portfolioCoin;
import static com.port.portcoin.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CoinAnalysisRepositoryImpl implements CoinAnalysisCustomRepository{

    private final JPAQueryFactory q;

    @Override
    public List<CoinDataResponse> findByCoinId() {
        List<CoinDataResponse> result = q
                .select(Projections.constructor(CoinDataResponse.class,
                        coin.symbol,
                        portfolioCoin.amount.sum()))
                .from(portfolioCoin)
                .join(portfolioCoin.coin, coin)
                .join(portfolioCoin.portfolio, portfolio)
                .join(portfolio.user, user)
                .groupBy(coin.symbol)
                .orderBy(portfolioCoin.amount.sum().desc())
                .fetch();

        return result;
    }
}
