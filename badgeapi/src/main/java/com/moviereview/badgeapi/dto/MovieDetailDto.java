package com.moviereview.badgeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.io.Serializable;

@Data
public class MovieDetailDto implements Serializable { // 'implements Serializable' 추가
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Integer id;

    // 칭호 계산에 필요한 "genres" 필드
    @JsonProperty("genres")
    private List<GenreDto> genres;

    // poster_path, title 등 다른 필드는 칭호 계산에 필요 없으므로 생략
}