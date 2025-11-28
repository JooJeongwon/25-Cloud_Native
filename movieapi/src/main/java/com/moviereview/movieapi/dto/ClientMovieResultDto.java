package com.moviereview.movieapi.dto;

import java.util.List;

// 클라이언트(React)에게 '반환'할 DTO
public record ClientMovieResultDto(
                Integer id,
                String title,
                String originalTitle,
                String overview,
                String posterUrl,
                String releaseDate,
                Double voteAverage,

                List<String> genres) {
}