package com.moviereview.movieapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record MovieResultDto(
        @JsonProperty("id") Integer id,
        @JsonProperty("title") String title,
        @JsonProperty("original_title") String originalTitle,
        @JsonProperty("overview") String overview,

        // JSON의 "poster_path"를 Java의 posterUrl 변수에 매핑
        @JsonProperty("poster_path") String posterUrl,

        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("vote_average") Double voteAverage,
        @JsonProperty("genre_ids") List<Integer> genreIds) {
}