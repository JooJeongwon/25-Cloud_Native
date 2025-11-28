package com.moviereview.badgeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.io.Serializable;

@Data // @Getter, @Setter, @NoArgsConstructor 등을 포함
public class GenreDto implements Serializable { // 'implements Serializable' 추가

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
}