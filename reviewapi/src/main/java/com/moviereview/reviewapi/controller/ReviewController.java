package com.moviereview.reviewapi.controller;

import com.moviereview.reviewapi.dto.ReviewRequestDto;
import com.moviereview.reviewapi.dto.ReviewResponseDto;
import com.moviereview.reviewapi.dto.ReviewDetailDto; // DTO import
import com.moviereview.reviewapi.entity.Review;
import com.moviereview.reviewapi.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews") // http://localhost:8081/reviews 로 시작
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;

        System.out.println("--- [SUCCESS] 최신 ReviewController (V2)가 로드되었습니다! ---");
    }

    /**
     * 1. 리뷰 작성
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody ReviewRequestDto requestDto,
            // Gateway가 넣어준 'X-User-Id' 헤더 값을 'userId' 변수에 받음
            @RequestHeader("X-User-Id") String userId) {
        // (getUserIdFromSession 호출 로직 삭제)
        Review review = reviewService.createReview(requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ReviewResponseDto(review));
    }

    /**
     * 2. 특정 영화의 리뷰 목록 조회
     */
    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByMovieId(
            @PathVariable Integer movieId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByMovieId(movieId).stream()
                .map(ReviewResponseDto::new) // Entity -> DTO 변환
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviews);
    }

    /**
     * 3. 내가 쓴 리뷰 목록 조회 (마이페이지용)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDetailDto>> getReviewsByUserId(
            @PathVariable String userId) {
        // ⭐️ 2. (수정) reviewService.getReviewDetailsByUserId(userId) 호출
        List<ReviewDetailDto> reviewDetails = reviewService.getReviewDetailsByUserId(userId);

        // ⭐️ 3. (수정) ReviewDetailDto 리스트를 반환
        return ResponseEntity.ok(reviewDetails);
    }

    /**
     * 4. 리뷰 수정 (@RequestParam 추가)
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto requestDto,
            @RequestHeader("X-User-Id") String userId // 헤더로 변경
    ) {
        Review review = reviewService.updateReview(reviewId, requestDto, userId);
        return ResponseEntity.ok(new ReviewResponseDto(review));
    }

    /**
     * 5. 리뷰 삭제 (@RequestParam 추가)
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("X-User-Id") String userId // 헤더로 변경
    ) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}