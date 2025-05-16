package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.DateCountResponse;
import com.port.portcoin.domain.analysis.dto.response.UserSummeryResponse;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.port.portcoin.domain.user.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class AnalysisRepositoryImpl implements AnalysisCustomRepository {

    private final JPAQueryFactory q;

    @Override
    public List<UserSummeryResponse> findByNMonth() {
        LocalDate now = LocalDate.now();
        return Stream.of(3, 6, 9, 12)
                .map(month -> new UserSummeryResponse(
                        month + " months",
                        q.select(user.count())
                                .from(user)
                                .where(user.createdAt.goe(now.minusMonths(month).atStartOfDay()))
                                .fetchOne()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<DateCountResponse> findByNWeek(LocalDate startDate) {
        // 주 단위로 자른 createdAt (PostgreSQL 기준)
        DateTemplate<LocalDateTime> weekStart = Expressions.dateTemplate(
                LocalDateTime.class,
                "date_trunc('week', {0})",
                user.createdAt
        );

        // count(*) 표현을 위한 별도 Expression
        NumberExpression<Long> userCount = user.id.count();

        List<Tuple> result = q
                .select(weekStart, userCount)
                .from(user)
                .where(user.createdAt.goe(startDate.atStartOfDay()))
                .groupBy(weekStart)
                .orderBy(weekStart.asc())
                .fetch();

        return result.stream()
                .map(tuple -> {
                    LocalDateTime weekStartTime = tuple.get(weekStart);
                    LocalDate weekDate = weekStartTime.toLocalDate();
                    Long count = tuple.get(userCount);
                    return new DateCountResponse(weekDate, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<DateCountResponse> findByMonth() {
        // 12개월 전부터 이번 달까지의 월 리스트 생성
        List<YearMonth> months = IntStream.rangeClosed(0, 11)
                .mapToObj(i -> YearMonth.now().minusMonths(i))
                .sorted() // 오름차순 정렬
                .collect(Collectors.toList());

        // QueryDSL로 이번 달 기준 12개월 동안의 가입자 데이터 조회
        DateTemplate<LocalDateTime> monthStart = Expressions.dateTemplate(
                LocalDateTime.class,
                "date_trunc('month', {0})",
                user.createdAt
        );

        List<Tuple> result = q
                .select(monthStart, user.count())
                .from(user)
                .where(user.createdAt.goe(LocalDate.now().minusMonths(12).withDayOfMonth(1).atStartOfDay()))
                .groupBy(monthStart)
                .fetch();

        // 결과 매핑: 월별 카운트를 Map으로 저장
        Map<YearMonth, Long> resultMap = result.stream()
                .collect(Collectors.toMap(
                        tuple -> YearMonth.from(tuple.get(monthStart)),
                        tuple -> tuple.get(user.count())
                ));

        // 모든 월 리스트를 기준으로 누락된 달은 0 처리
        return months.stream()
                .map(month -> new DateCountResponse(month.atDay(1), resultMap.getOrDefault(month, 0L)))
                .collect(Collectors.toList());
    }
}

