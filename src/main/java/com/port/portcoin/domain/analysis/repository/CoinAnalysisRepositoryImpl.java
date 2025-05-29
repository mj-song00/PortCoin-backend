package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;
import com.port.portcoin.domain.analysis.dto.response.HoldingDistributionResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.port.portcoin.domain.coin.entity.QCoin.coin;
import static com.port.portcoin.domain.portfolio.entity.QPortfolio.portfolio;
import static com.port.portcoin.domain.portfoliocoin.entity.QPortfolioCoin.portfolioCoin;
import static com.port.portcoin.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class CoinAnalysisRepositoryImpl implements CoinAnalysisCustomRepository {

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

    @Override
    public List<HoldingDistributionResponse> findByUserDistribution() {
        List<Tuple> userCoinCounts = q
                .select(user.id, portfolioCoin.coin.id.countDistinct())
                .from(user)
                .join(portfolio).on(portfolio.user.id.eq(user.id))
                .join(portfolioCoin).on(portfolioCoin.portfolio.portfolioId.eq(portfolio.portfolioId))
                .groupBy(user.id)
                .fetch();

        // 2. 구간별로 분포 집계
        Map<String, Long> distribution = userCoinCounts.stream()
                .map(tuple -> {
                    Long count = tuple.get((portfolioCoin.coin.id.countDistinct()));
                    if (count == 1) return "1개";
                    else if (count >= 2 && count <= 3) return "2~3개";
                    else if (count >= 4 && count <= 5) return "4~5개";
                    else return "6개 이상";
                })
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long totalUsers = userCoinCounts.size();

        // 3. DTO로 변환 + 비율 계산
        return distribution.entrySet().stream()
                .map(entry -> new HoldingDistributionResponse(
                        entry.getKey(),
                        entry.getValue(),
                        Math.round((entry.getValue() * 100.0 / totalUsers) * 10) / 10.0 // 소수점 1자리 반올림
                ))
                .sorted(Comparator.comparing(HoldingDistributionResponse::getRange)) // 정렬 필요시
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateRateOfReturn() {
        List<Tuple> userProfitRates = q
                .select(
                        user.id,
                        Expressions.cases()
                                .when(portfolioCoin.purchasePrice.multiply(portfolioCoin.amount).sum().eq(0.0))
                                .then(0.0)
                                .otherwise(
                                        portfolioCoin.currentPrice.multiply(portfolioCoin.amount).sum()
                                                .subtract(portfolioCoin.purchasePrice.multiply(portfolioCoin.amount).sum())
                                                .divide(portfolioCoin.purchasePrice.multiply(portfolioCoin.amount).sum())
                                )
                                .as("profitRate")
                )
                .from(user)
                .join(portfolio).on(user.id.eq(portfolio.user.id))
                .join(portfolioCoin).on(portfolio.portfolioId.eq(portfolioCoin.portfolio.portfolioId))
                .groupBy(user.id)
                .fetch();

        double averageProfitRate = userProfitRates.stream()
                .mapToDouble(tuple -> {
                    Double profitRate = tuple.get(1, Double.class);
                    return profitRate != null ? profitRate : 0.0;
                })
                .average()
                .orElse(0.0);

        return Math.round(averageProfitRate * 10000.0) / 100.0;  // 소수점 2자리 반올림
    }
}
