package com.port.portcoin.domain.external.naver.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsResponse {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;

    public NewsResponse(String title, String originallink, String link, String description, String pubDate) {
        this.title = title;
        this.originallink = originallink;
        this.link = link;
        this.description = description;
        this.pubDate = pubDate;
    }
}