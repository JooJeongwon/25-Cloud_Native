package com.moviereview.reviewapi.dto;

import com.moviereview.reviewapi.entity.Review;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ReviewDetailDto {
    private Long id;
    private String userId;
    private Double rating;
    private String content;
    private LocalDateTime createdAt;

    private Integer movieId; // 기존 movieId
    private String movieTitle; // 신규 영화 제목

    // Review 엔티티와 영화 제목을 합쳐서 DTO를 만드는 생성자
    public ReviewDetailDto(Review review, String movieTitle) {
        this.id = review.getId();
        this.userId = review.getUserId();
        this.rating = review.getRating();
        this.content = review.getContent();
        this.createdAt = review.getCreatedAt();
        this.movieId = review.getMovieId();
        this.movieTitle = movieTitle; // 영화 제목 추가
    }
}