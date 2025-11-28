package com.moviereview.reviewapi.repository;

import com.moviereview.reviewapi.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// 인터페이스로 선언하고 JpaRepository를 상속
// <어떤 Entity를, Primary Key의 타입은 무엇인지>
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // "MovieId로 모든 리뷰를 찾아줘"
    // SELECT * FROM review WHERE movie_id = ?
    List<Review> findByMovieId(Integer movieId);

    // "UserId로 모든 리뷰를 찾아줘"
    // SELECT * FROM review WHERE user_id = ?
    List<Review> findByUserId(String userId);
}