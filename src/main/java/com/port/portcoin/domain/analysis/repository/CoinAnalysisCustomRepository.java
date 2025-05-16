package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;

import java.util.List;

public interface CoinAnalysisCustomRepository {
    List<CoinDataResponse> findByCoinId();
}
