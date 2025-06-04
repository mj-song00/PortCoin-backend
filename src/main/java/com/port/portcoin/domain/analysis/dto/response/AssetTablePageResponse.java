package com.port.portcoin.domain.analysis.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class AssetTablePageResponse {
    private List<AssetTableResponseItem> contents;
    private int currentPage;
    private int totalPages;
    private long totalElements;


    public AssetTablePageResponse(List<AssetTableResponseItem> contents, int currentPage, int totalPages, long totalElements) {
        this.contents = contents;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }
}
