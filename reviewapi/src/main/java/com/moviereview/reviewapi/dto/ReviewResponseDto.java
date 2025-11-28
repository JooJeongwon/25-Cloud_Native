package com.moviereview.reviewapi.dto;

import com.moviereview.reviewapi.entity.Review;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ReviewResponseDto {
    private Long id;
    private String userId;
    private Integer movieId;
    private Double rating;
    private String content;
    private LocalDateTime createdAt;

    // 엔티티(Review)를 DTO(ReviewResponseDto)로 쉽게 변환하는 생성자
    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.movieId = review.getMovieId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
    }
}