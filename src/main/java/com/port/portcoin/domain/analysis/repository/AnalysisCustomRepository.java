package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.DateCountResponse;
import com.port.portcoin.domain.analysis.dto.response.UserSummeryResponse;

import java.time.LocalDate;
import java.util.List;

public interface AnalysisCustomRepository {

    List<UserSummeryResponse> findByNMonth();

    List<DateCountResponse> findByNWeek(LocalDate startDate);

    List<DateCountResponse> findByMonth();
}
