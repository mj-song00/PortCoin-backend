package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.UserSummeryResponse;

import java.util.List;

public interface AnalysisCustomRepository {

    List<UserSummeryResponse> findByNMonth();

}
