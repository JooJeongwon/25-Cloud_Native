package com.moviereview.badgeapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserBadge implements Serializable { // 'implements Serializable' 추가

    // serialVersionUID 추가
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 고유 ID

    private String userId; // 칭호 소유자 ID

    private String badgeName; // 칭호 이름 (예: "HORROR_EXPERT")

    private String description; // 칭호 설명 (예: "공포 영화 10편 이상 관람")

    @CreationTimestamp
    private LocalDateTime createdAt; // 칭호 획득일
}