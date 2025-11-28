package com.moviereview.movieapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MovieSearchResponseDto(
        @JsonProperty("page") Integer page,

        // "results" 필드에는 위에서 만든 MovieResultDto의 '리스트'가 들어갑니다.
        @JsonProperty("results") List<MovieResultDto> results,

        @JsonProperty("total_pages") Integer totalPages,
        @JsonProperty("total_results") Integer totalResults) {
}