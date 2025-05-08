package com.port.portcoin.domain.external.naver;

import com.port.portcoin.domain.external.naver.dto.response.NaverNewsList;
import com.port.portcoin.domain.external.naver.dto.response.NewsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Slf4j
public class NaverNews {
    private final RestClient naverRestClient;
    private final RedisTemplate<String, List<NewsResponse>> redisTemplate;


    public NaverNews(RestClient naverRestClient, RedisTemplate<String, List<NewsResponse>> redisTemplate) {
        this.naverRestClient = naverRestClient;
        this.redisTemplate = redisTemplate;
    }

    public List<NewsResponse> getNews() {
        // Redis에서 데이터를 가져옵니다
        List<NewsResponse> newsData = redisTemplate.opsForValue().get("News");

        // Redis에 데이터가 없으면 새로 가져옵니다
        if (newsData == null) {
            List<NewsResponse> allNews = new ArrayList<>();

            // 네이버 뉴스 API에서 데이터를 가져옵니다
            NaverNewsList response = naverRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("query", "코인,경기,해외,주식")
                            .queryParam("display", 10)
                            .queryParam("start", 1)
                            .queryParam("sort", "date")
                            .build())
                    .retrieve()
                    .body(NaverNewsList.class);

            // 응답이 null이 아니고, 뉴스 아이템이 존재하면 처리
            if (response != null && response.getItems() != null) {
                for (NewsResponse news : response.getItems()) {
                    // "칼럼, 이코노믹스"가 포함된 뉴스는 제외
                    if (news.getTitle().contains("칼럼") ||news.getTitle().contains("이코노믹스")) {
                        continue;
                    }
                    allNews.add(news);  // allNews 리스트에 추가
                }
            }

            // 제목 기준으로 중복 제거
            newsData = allNews.stream()
                    .filter(distinctByTitle(news -> news.getTitle()))  // 제목 중복 제거
                    .collect(Collectors.toList());

            // 처리된 데이터를 Redis에 저장 (5분간 유효)
            redisTemplate.opsForValue().set("News", newsData, 5, TimeUnit.MINUTES);
        }

        return newsData;  // 최종 결과 리턴
    }


    // 5분마다 캐시 갱신
    @Scheduled(fixedRate = 300000) // 60,000ms = 1분
    public void refreshNews() {
        // 외부 API에서 데이터를 가져와서 Redis에 갱신

        List<NewsResponse> refreshedNewsData = naverRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query","코인,경기,해외,주식" )
                        .queryParam("display", 10)
                        .queryParam("start", 1)
                        .queryParam("sort", "date")
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        // Redis에 새로운 데이터 저장
        redisTemplate.opsForValue().set("News", refreshedNewsData, 5, TimeUnit.MINUTES);
    }

    // 제목 기준으로 중복 제거하는 메서드
    private static Predicate<NewsResponse> distinctByTitle(Function<NewsResponse, String> keyExtractor) {
        Set<String> seen = new HashSet<>();
        return news -> seen.add(keyExtractor.apply(news));
    }
}
