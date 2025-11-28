package com.moviereview.movieapi.dto;

import java.util.List;

// 클라이언트(React)에게 '반환'할 최종 검색 응답 DTO
public record ClientMovieSearchResponseDto(
                Integer page,
                List<ClientMovieResultDto> results,
                Integer totalPages,
                Integer totalResults) {
}