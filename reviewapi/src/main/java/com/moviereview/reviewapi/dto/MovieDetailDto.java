package com.moviereview.reviewapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class MovieDetailDto implements Serializable {
    private static final long serialVersionUID = 1L;
    @JsonProperty("id")
    private Integer id;

    // movieapi가 "poster_path"로 보내주므로, 이 키를 매핑
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("title")
    private String title; // 영화 제목

    @JsonProperty("genres")
    private List<GenreDto> genres;
}