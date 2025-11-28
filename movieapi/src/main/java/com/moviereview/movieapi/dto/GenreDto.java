package com.moviereview.movieapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public record GenreDto(
                @JsonProperty("id") Integer id,
                @JsonProperty("name") String name) implements Serializable {
        private static final long serialVersionUID = 1L;
}