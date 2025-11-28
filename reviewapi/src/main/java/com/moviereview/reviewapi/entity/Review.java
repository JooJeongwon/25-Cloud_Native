package com.moviereview.reviewapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity // 이 클래스가 DB 테이블임을 선언
@Getter // Lombok: getter 자동 생성
@Setter // Lombok: setter 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성
public class Review {

    @Id // 이 필드가 Primary Key(고유 식별자)임을 선언
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID를 자동으로 생성(auto-increment)
    private Long id; // 리뷰 고유 ID

    // 기획했던 핵심 기능들
    private String userId; // 리뷰 작성자 ID
    private Integer movieId; // 영화 ID (TMDB의 ID)
    private Double rating; // 별점
    private String content; // 리뷰 내용

    @CreationTimestamp // 데이터 생성 시 자동으로 현재 시간 저장
    private LocalDateTime createdAt;
}