package com.moviereview.reviewapi.service;

import com.moviereview.reviewapi.dto.ReviewRequestDto;
import com.moviereview.reviewapi.dto.MovieDetailDto; // ⭐️ DTO import
import com.moviereview.reviewapi.dto.ReviewDetailDto;
import com.moviereview.reviewapi.entity.Review;
import com.moviereview.reviewapi.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Value; // ⭐️ @Value import
import org.springframework.boot.web.client.RestTemplateBuilder; // RestTemplateBuilder import
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate; // RestTemplate import
// import org.springframework.web.reactive.function.client.WebClient; (삭제)
// import reactor.core.publisher.Mono; (삭제)

import java.util.List;
import java.util.stream.Collectors; // Collectors import
import java.lang.RuntimeException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final RestTemplate restTemplate; // WebClient -> RestTemplate

    private final String movieApiBaseUrl;

    // 생성자 수정 (@Value 추가)
    public ReviewService(
            ReviewRepository reviewRepository,
            RestTemplateBuilder builder,
            @Value("${movieapi.baseurl}") String movieApiBaseUrl) {
        this.reviewRepository = reviewRepository;
        this.restTemplate = builder.build();
        this.movieApiBaseUrl = movieApiBaseUrl; // 주소 초기화
    }

    /**
     * 1. 리뷰 작성
     */
    @Transactional
    public Review createReview(ReviewRequestDto requestDto, String userId) {

        // verificationapi를 호출할 URL
        String verifyUrl = String.format(
                "http://verificationapi:8000/api/verify?userId=%s&movieId=%d",
                userId,
                requestDto.getMovieId());

        try {
            // 시청 인증 여부 실시간 검증
            Boolean hasWatched = restTemplate.getForObject(verifyUrl, Boolean.class);

            // 만약 보지 않았다면, 여기서 Exception을 발생시키고 종료
            if (hasWatched == null || !hasWatched) {
                throw new RuntimeException("이 영화를 시청한 사용자만 리뷰를 작성할 수 있습니다.");
            }
        } catch (Exception e) {
            // verificationapi가 4xx/5xx 오류를 반환하거나 꺼져있을 때
            throw new RuntimeException("시청 인증 서비스 확인 중 오류 발생: " + e.getMessage());
        }

        Review review = new Review();
        review.setUserId(userId); // 임시로 "user-test" 같은 값을 쓰거나, 파라미터로 받음
        review.setMovieId(requestDto.getMovieId());
        review.setRating(requestDto.getRating());
        review.setContent(requestDto.getContent());

        return reviewRepository.save(review);
    }

    /**
     * 2. 특정 영화의 모든 리뷰 조회
     */
    public List<Review> getReviewsByMovieId(Integer movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    /**
     * 3. 특정 사용자의 모든 리뷰 조회
     */
    public List<Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    // 마이페이지를 위한 "리뷰 + 영화 제목" 조회 서비스
    public List<ReviewDetailDto> getReviewDetailsByUserId(String userId) {
        // 1. DB에서 내가 쓴 리뷰 목록을 가져옴
        List<Review> myReviews = reviewRepository.findByUserId(userId);

        // 2. 각 리뷰의 movieId를 사용해 movieapi에서 영화 제목을 가져옴
        return myReviews.stream().map(review -> {
            String movieTitle = " (영화 정보 없음)"; // 기본값
            try {
                // 3. RestTemplate으로 movieapi 호출
                // (GET http://movieapi:8089/movies/496243)
                String url = movieApiBaseUrl + "/movies/" + review.getMovieId();
                MovieDetailDto movie = restTemplate.getForObject(url, MovieDetailDto.class);

                if (movie != null && movie.getTitle() != null) {
                    movieTitle = movie.getTitle();
                }
            } catch (Exception e) {
                // movieapi가 죽었거나, 영화를 못 찾아도 무시
                System.err.println("Movie API 호출 실패: " + e.getMessage());
            }

            // 4. Review 엔티티와 movieTitle을 합쳐서 새 DTO 생성
            return new ReviewDetailDto(review, movieTitle);

        }).collect(Collectors.toList());
    }

    /**
     * 4. 리뷰 수정
     */
    @Transactional
    public Review updateReview(Long reviewId, ReviewRequestDto requestDto, String currentUserId) { // 1. 수정할 리뷰를 DB에서 찾음
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다. id=" + reviewId));

        // 2. 보안 로직: 리뷰 작성자와 현재 요청자가 같은지 확인
        if (!review.getUserId().equals(currentUserId)) {
            throw new RuntimeException("리뷰를 수정할 권한이 없습니다.");
        }

        // 3. 내용을 업데이트
        review.setRating(requestDto.getRating());
        review.setContent(requestDto.getContent());

        // 4. save (JPA는 ID가 이미 존재하면 'update'를 실행)
        return reviewRepository.save(review);
    }

    /**
     * 5. 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long reviewId, String currentUserId) { // 1. 삭제할 리뷰를 DB에서 찾음
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 리뷰를 찾을 수 없습니다. id=" + reviewId));

        // 2. 보안 로직: 리뷰 작성자와 현재 요청자가 같은지 확인
        if (!review.getUserId().equals(currentUserId)) {
            throw new RuntimeException("리뷰를 삭제할 권한이 없습니다.");
        }

        // 3. 삭제
        reviewRepository.deleteById(reviewId);
    }
}