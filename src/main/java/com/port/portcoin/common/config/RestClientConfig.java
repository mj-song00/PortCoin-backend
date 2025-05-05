package com.port.portcoin.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${coin.api.key}")
    private String key;

    @Value("${naver.api.clientId}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;



    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://api.coingecko.com/api/v3")
                .defaultHeader("accept","application/json")
                .defaultHeader("x-cg-demo-api-key" ,key)
                .build();
    }

    @Bean
    public RestClient naverRestClient(){
        return RestClient.builder()
                .baseUrl("https://openapi.naver.com/v1/search/news.json")
                .defaultHeader("X-Naver-Client-Id",clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .build();
    }
}
