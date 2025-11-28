package com.moviereview.badgeapi.service;

import com.moviereview.badgeapi.dto.MovieDetailDto;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component // @Service 대신 @Component 사용
public class MovieApiClient {

    private final WebClient webClient;

    public MovieApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * "movieDetails" 캐시 그룹에 movieId를 키로 하여 영화 정보를 캐시
     */
    @Cacheable(value = "movieDetails", key = "#movieId")
    public MovieDetailDto getMovieDetail(Integer movieId) {
        System.out.println("movieapi 호출 (캐시 없음): " + movieId);
        return webClient.get()
                .uri("http://movieapi:8089/movies/" + movieId)
                .retrieve()
                .bodyToMono(MovieDetailDto.class)
                .block(); // 트랜잭션 밖에서 호출되므로 안전함
    }
}