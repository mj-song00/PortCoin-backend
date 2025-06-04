package com.port.portcoin.domain.analysis.repository;

import com.port.portcoin.domain.analysis.dto.response.AssetTableResponseItem;
import com.port.portcoin.domain.analysis.dto.response.PieChartResponse;
import com.port.portcoin.domain.analysis.dto.response.TotalAssetResponse;
import com.port.portcoin.domain.analysis.dto.response.UserAssetSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssetCustomRepository {

    List<PieChartResponse> getPieChart();

    Page<AssetTableResponseItem> getTable(Pageable pageable);

    TotalAssetResponse getAverage();

    List<UserAssetSummary> getBottom();

    List<UserAssetSummary> getTop();
}
