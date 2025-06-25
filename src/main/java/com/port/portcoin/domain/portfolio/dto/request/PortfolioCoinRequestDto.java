package com.port.portcoin.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PortfolioCoinRequestDto {
    @NotNull(message = "코인 ID는 필수입니다.")
    private Long coinId;

    @NotNull(message = "수량은 필수입니다.")
    private Double amount;

    @NotNull(message = "구매 가격은 필수입니다.")
    private Double purchasePrice;

    @NotNull(message = "구매일은 필수입니다.")
    private String purchaseDate;
}
