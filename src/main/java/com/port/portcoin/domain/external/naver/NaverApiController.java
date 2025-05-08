package com.port.portcoin.domain.external.naver;

import com.port.portcoin.common.response.ApiResponse;
import com.port.portcoin.common.response.ApiResponseEnum;
import com.port.portcoin.domain.external.naver.dto.response.NewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/naver")
@Tag(name = "Naver", description = "Naver news API")
@Slf4j
public class NaverApiController {

    private final RedisTemplate<String, List<NewsResponse>> redisTemplate;
    private final NaverNews naverNews;

    @Operation(summary = "네이버 뉴스 검색", description = "네이버 경제부분 뉴스를 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<NewsResponse>>> getNews() {
        List<NewsResponse> news = redisTemplate.opsForValue().get("News");

        if (news == null) {
            news = naverNews.getNews();
            redisTemplate.opsForValue().set("News", news, 5, TimeUnit.MINUTES);
        }

        ApiResponse<List<NewsResponse>> response = ApiResponse.successWithData(news, ApiResponseEnum.GET_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
