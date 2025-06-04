package com.port.portcoin.domain.analysis.service;

import com.port.portcoin.common.exception.BaseException;
import com.port.portcoin.common.exception.ExceptionEnum;
import com.port.portcoin.domain.analysis.dto.response.*;
import com.port.portcoin.domain.analysis.repository.AssetCustomRepository;
import com.port.portcoin.domain.user.dto.AuthUser;
import com.port.portcoin.domain.user.entity.User;
import com.port.portcoin.domain.user.enums.UserRole;
import com.port.portcoin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final AssetCustomRepository assetRepository;
    private final UserRepository userRepository;

    public List<PieChartResponse> getPieChart(AuthUser authUser) {
        validateAdminUser(authUser.getId());
        return assetRepository.getPieChart();
    }

    public Page<AssetTableResponseItem> getSummery(Pageable pageable, AuthUser authUser) {
        validateAdminUser(authUser.getId());
        return assetRepository.getTable(pageable);
    }

    public TotalAssetResponse getAverage(AuthUser authUser) {
        validateAdminUser(authUser.getId());
        return assetRepository.getAverage();
    }

    public List<UserAssetSummary> getBottom(AuthUser authUser) {
        validateAdminUser(authUser.getId());
        return assetRepository.getBottom();
    }

    public List<UserAssetSummary> getTop(AuthUser authUser) {
        validateAdminUser(authUser.getId());
        return assetRepository.getTop();
    }

    private void validateAdminUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            throw new BaseException(ExceptionEnum.NOT_ADMIN_ROLE);
        }
    }

}
