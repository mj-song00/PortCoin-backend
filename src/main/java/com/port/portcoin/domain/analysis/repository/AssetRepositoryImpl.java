package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.AssetTableResponseItem;
import com.port.portcoin.domain.analysis.dto.response.PieChartResponse;
import com.port.portcoin.domain.analysis.dto.response.TotalAssetResponse;
import com.port.portcoin.domain.analysis.dto.response.UserAssetSummary;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.port.portcoin.domain.coin.entity.QCoin.coin;
import static com.port.portcoin.domain.portfolio.entity.QPortfolio.portfolio;
import static com.port.portcoin.domain.portfoliocoin.entity.QPortfolioCoin.portfolioCoin;

@Repository
@RequiredArgsConstructor
public class AssetRepositoryImpl implements AssetCustomRepository{

    private final JPAQueryFactory q;

    @Override
    public List<PieChartResponse> getPieChart() {
                // 전체 자산 합계 먼저 조회
                Double totalAsset = q
                .select(portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum())
                .from(portfolioCoin)
                .fetchOne();

        if (totalAsset == null || totalAsset == 0) {
            return Collections.emptyList();
        }

        // 코인별 합계와 비율 가져오기
        return q
                .select(Projections.constructor(
                        PieChartResponse.class,
                        coin.symbol,
                        coin.name,
                        portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum().as("totalAssetValue"),
                        portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum().divide(totalAsset).multiply(100.0).as("ratio")
                ))
                .from(portfolioCoin)
                .join(portfolioCoin.coin, coin)
                .groupBy(coin.symbol, coin.name)
                .orderBy(portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum().desc())
                .limit(10)
                .fetch();
    }

    @Override
    public Page<AssetTableResponseItem> getTable(Pageable pageable) {
        List<AssetTableResponseItem> results = q
                .select(Projections.constructor(
                        AssetTableResponseItem.class,
                        coin.symbol,
                        coin.name,
                        portfolioCoin.amount.sum(),
                        portfolioCoin.currentPrice.avg(),
                        portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum()
                ))
                .from(portfolioCoin)
                .join(portfolioCoin.coin, coin)
                .groupBy(coin.symbol, coin.name)
                .orderBy(portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = q
                .select(coin.id.countDistinct())
                .from(portfolioCoin)
                .join(portfolioCoin.coin, coin)
                .fetchOne();
        long total = (totalCount != null) ? totalCount : 0L;

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public TotalAssetResponse getAverage() {
        List<Double> userTotals = q
                .select(portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum())
                .from(portfolioCoin)
                .groupBy(portfolioCoin.portfolio.user.id) // 유저별로 그룹핑
                .fetch();

// 2) 평균 구하기 (Java 코드로)
        double averageAsset = 0.0;
        if (!userTotals.isEmpty()) {
            averageAsset = userTotals.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
        }
        return new TotalAssetResponse(averageAsset);
    }

    @Override
    public List<UserAssetSummary> getBottom() {
        // 1. 유저별 총 자산 구하기
        List<Tuple> userAssets = q
                .select(
                        portfolio.user.id,
                        portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum()
                )
                .from(portfolioCoin)
                .join(portfolioCoin.portfolio, portfolio)
                .groupBy(portfolio.user.id)
                .fetch();

        // 2. Java에서 정렬 및 하위 10% 필터링
        List<UserAssetSummary> sorted = userAssets.stream()
                .filter(tuple -> tuple.get(1, Double.class) != null)
                .map(tuple -> new UserAssetSummary(
                        tuple.get(0, Long.class),   // userId
                        tuple.get(1, Double.class)  // totalAsset
                ))
                .sorted(Comparator.comparingDouble(UserAssetSummary::getTotalAsset)) // 오름차순
                .toList();

        int totalCount = sorted.size();
        int tenPercentCount = (int) Math.ceil(totalCount * 0.1);

        return sorted.subList(0, Math.min(tenPercentCount, totalCount));
    }

    @Override
    public List<UserAssetSummary> getTop() {
        return q
                .select(Projections.constructor(
                        UserAssetSummary.class,
                        portfolio.user.id,
                        portfolioCoin.amount.multiply(portfolioCoin.currentPrice).sum()
                ))
                .from(portfolioCoin)
                .join(portfolioCoin.portfolio, portfolio)
                .groupBy(portfolio.user.id)
                .fetch();
    }
}
