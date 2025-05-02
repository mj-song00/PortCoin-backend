package com.port.portcoin.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    private String name;
}
