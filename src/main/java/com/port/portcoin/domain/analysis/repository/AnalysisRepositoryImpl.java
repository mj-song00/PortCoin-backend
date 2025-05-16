package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.UserSummeryResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
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
}
