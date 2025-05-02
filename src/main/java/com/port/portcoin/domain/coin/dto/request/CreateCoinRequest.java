package com.port.portcoin.domain.coin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCoinRequest {

    @NotBlank
    private String symbol;

    @NotBlank
    private String name;

}
