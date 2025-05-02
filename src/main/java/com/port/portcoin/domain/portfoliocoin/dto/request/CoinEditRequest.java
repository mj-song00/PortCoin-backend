package com.port.portcoin.domain.portfoliocoin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CoinEditRequest {
    @NotNull(message = "하나의 포트폴리오를 선택해주세요")
    private Long portfolioId;

    private List<CoinAdd> toAdd;
    private List<CoinUpdate> toUpdate;
    private List<Long> toDelete;
}
