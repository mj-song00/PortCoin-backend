package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DateCountResponse {
    private LocalDate date;
    private Long count;

    public DateCountResponse(LocalDate date, Long count){
        this.date = date;
        this.count = count;
    }
}
