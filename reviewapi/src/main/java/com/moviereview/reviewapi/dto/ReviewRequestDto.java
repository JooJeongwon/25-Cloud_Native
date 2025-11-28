package com.moviereview.reviewapi.dto;

import lombok.Getter;

@Getter
public class ReviewRequestDto {
    private Integer movieId; // 영화 ID
    private Double rating; // 별점
    private String content; // 리뷰 내용
}