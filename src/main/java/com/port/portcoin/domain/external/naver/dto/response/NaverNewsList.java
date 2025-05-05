package com.port.portcoin.domain.external.naver.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NaverNewsList {
    private List<NewsResponse> items;


    public List<NewsResponse> getItems() {
        return items;
    }
}
