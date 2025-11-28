package com.moviereview.movieapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import com.moviereview.movieapi.dto.*; // DTO 전체 import
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors; // Collectors import

@Service
public class MovieService {

        private final WebClient webClient;
        private final String apiKey;
        private final String imageBaseUrl;
        private final String tmdbBaseUrl;

        private Map<Integer, String> genreMap = new ConcurrentHashMap<>();

        // AppConfig.java 없이 @Value를 생성자에서 직접 주입
        public MovieService(
                        WebClient.Builder webClientBuilder,
                        @Value("${tmdb.api.key}") String apiKey,
                        @Value("${tmdb.api.baseurl}") String tmdbBaseUrl,
                        @Value("${tmdb.api.imagebaseurl}") String imageBaseUrl) {
                this.apiKey = apiKey;
                this.imageBaseUrl = imageBaseUrl;
                this.tmdbBaseUrl = tmdbBaseUrl; // tmdbBaseUrl 필드 초기화
                this.webClient = webClientBuilder.baseUrl(this.tmdbBaseUrl).build();
        }

        @PostConstruct
        public void loadGenres() {
                webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/genre/movie/list")
                                                .queryParam("api_key", apiKey)
                                                .queryParam("language", "ko-KR")
                                                .build())
                                .retrieve()
                                .bodyToMono(GenreListResponseDto.class)
                                .map(GenreListResponseDto::genres)
                                .subscribe(genres -> genres.forEach(genre -> genreMap.put(genre.id(), genre.name())));
                System.out.println("장르 맵 로딩 시작...");
        }

        // searchMovies (검색 API)
        public Mono<ClientMovieSearchResponseDto> searchMovies(String query) {
                return webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/search/movie")
                                                .queryParam("api_key", apiKey)
                                                .queryParam("query", query)
                                                .queryParam("language", "ko-KR")
                                                .build())
                                .retrieve()
                                .bodyToMono(MovieSearchResponseDto.class)
                                .map(responseDto -> {
                                        List<ClientMovieResultDto> transformedResults = responseDto.results().stream()
                                                        .map(movie -> {
                                                                List<String> genreNames = movie.genreIds().stream()
                                                                                .map(id -> genreMap.getOrDefault(id,
                                                                                                "Unknown"))
                                                                                .toList();

                                                                String fullPosterUrl = (movie.posterUrl() == null
                                                                                || imageBaseUrl == null)
                                                                                                ? null
                                                                                                : imageBaseUrl + movie
                                                                                                                .posterUrl();

                                                                return new ClientMovieResultDto(
                                                                                movie.id(), movie.title(),
                                                                                movie.originalTitle(), movie.overview(),
                                                                                fullPosterUrl, movie.releaseDate(),
                                                                                movie.voteAverage(), genreNames);
                                                        })
                                                        .toList();

                                        return new ClientMovieSearchResponseDto(
                                                        responseDto.page(), transformedResults,
                                                        responseDto.totalPages(), responseDto.totalResults());
                                });
        }

        // getMovieDetail (상세 API)
        public Mono<MovieDetailDto> getMovieDetail(String movieId) {
                return webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                                .path("/movie/" + movieId)
                                                .queryParam("api_key", apiKey)
                                                .queryParam("language", "ko-KR")
                                                .build())
                                .retrieve()
                                .bodyToMono(MovieDetailDto.class)
                                .map(movie -> { // 외부 데이터를 내부 표준으로 변환
                                        String fullPosterUrl = (movie.posterUrl() == null || imageBaseUrl == null)
                                                        ? null
                                                        : imageBaseUrl + movie.posterUrl();

                                        return new MovieDetailDto(
                                                        movie.id(),
                                                        movie.title(),
                                                        movie.overview(),
                                                        fullPosterUrl, // 변환된 URL
                                                        movie.releaseDate(),
                                                        movie.voteAverage(),
                                                        movie.genres(),
                                                        movie.runtime());
                                });
        }
}