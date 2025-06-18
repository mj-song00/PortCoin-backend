package com.port.portcoin.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    private List<PortfolioCoinRequestDto> coins;
}
