package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class UserProfitResponse {
    private final List<UserProfitResponseItem> items;
    private final int currentPage; // 현재 페이지 번호
    private final int totalPages; // 총 페이지 수
    private final long totalElements; // 총 요소 수

    public UserProfitResponse(List<UserProfitResponseItem> items, int currentPage, int totalPages, long totalElements ){
        this.items =items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
