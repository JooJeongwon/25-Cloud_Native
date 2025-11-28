package com.moviereview.badgeapi.repository;

import com.moviereview.badgeapi.entity.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// JpaRepository<어떤 Entity를, Primary Key의 타입은 무엇인지>
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    // "UserId로 모든 칭호를 찾아줘"
    // JPA가 메소드 이름을 보고 자동으로 SQL을 생성
    // SELECT * FROM user_badge WHERE user_id = ?
    List<UserBadge> findByUserId(String userId);

    Optional<UserBadge> findByUserIdAndBadgeName(String userId, String badgeName);
}