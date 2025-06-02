package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.CoinDataResponse;
import com.port.portcoin.domain.analysis.dto.response.HoldingDistributionResponse;
import com.port.portcoin.domain.analysis.dto.response.UserProfitResponseItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CoinAnalysisCustomRepository {
    List<CoinDataResponse> findByCoinId();

    List<HoldingDistributionResponse> findByUserDistribution();

    Double calculateRateOfReturn();

    Page<UserProfitResponseItem> calculateTop5(Pageable pageable);
}
