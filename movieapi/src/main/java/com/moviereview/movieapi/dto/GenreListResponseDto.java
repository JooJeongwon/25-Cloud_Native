package com.moviereview.movieapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// TMDB의 /genre/movie/list API 응답을 위한 DTO
public record GenreListResponseDto(
        @JsonProperty("genres") List<GenreDto> genres) {
}