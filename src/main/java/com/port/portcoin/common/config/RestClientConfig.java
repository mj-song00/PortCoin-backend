package com.port.portcoin.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${coin.api.key}")
    private String key;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://api.coingecko.com/api/v3")
                .defaultHeader("accept","application/json")
                .defaultHeader("x-cg-demo-api-key" ,key)
                .build();
    }
}
