package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

@Getter
public class UserSummeryResponse {
    private Long total;
    private String period;


    public UserSummeryResponse( String period, Long total) {
       this.period = period;
       this.total = total;
    }
}
