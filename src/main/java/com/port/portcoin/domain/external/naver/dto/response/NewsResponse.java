package com.port.portcoin.domain.external.naver.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsResponse {
    @JsonProperty("title")
    private String title;
    @JsonProperty("originallink")
    private String originalLink;

    public NewsResponse(String title, String originalLink){
        this.title = title;
        this.originalLink = originalLink;
    }
}
