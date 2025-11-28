package com.moviereview.badgeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty; // JSON 필드 매핑
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VerificationDto {

    // Python의 'user_id' (snake_case)를 Java의 'userId' (camelCase)로 매핑
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("movie_id")
    private Integer movieId;

    // id, status 등 다른 필드도 필요하면 추가
}