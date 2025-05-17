package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;
import com.port.portcoin.domain.analysis.dto.response.HoldingDistributionResponse;

import java.util.List;

public interface CoinAnalysisCustomRepository {
    List<CoinDataResponse> findByCoinId();

    List<HoldingDistributionResponse> findByUserDistribution();
}
