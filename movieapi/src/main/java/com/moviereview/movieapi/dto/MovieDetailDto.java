package com.moviereview.movieapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

public record MovieDetailDto(
        @JsonProperty("id") Integer id,
        @JsonProperty("title") String title,
        @JsonProperty("overview") String overview,
        @JsonProperty(value = "poster_path") String posterUrl,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("vote_average") Double voteAverage,
        @JsonProperty("genres") List<GenreDto> genres,
        @JsonProperty("runtime") Integer runtime) implements Serializable {
    private static final long serialVersionUID = 1L;

    // .map() 로직이 사용할 수동 생성자
    public MovieDetailDto(Integer id, String title, String overview, String posterUrl, String releaseDate,
            Double voteAverage, List<GenreDto> genres, Integer runtime) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.posterUrl = posterUrl;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.genres = genres;
        this.runtime = runtime;
    }
}