package com.moviereview.badgeapi.service;

import com.moviereview.badgeapi.entity.UserBadge;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BadgeService {

    private final BadgeQueryService badgeQueryService;
    private final BadgeCalculator badgeCalculator;

    // 컨트롤러가 사용할 서비스들만 주입
    public BadgeService(BadgeQueryService badgeQueryService, BadgeCalculator badgeCalculator) {
        this.badgeQueryService = badgeQueryService;
        this.badgeCalculator = badgeCalculator;
    }

    // (컨트롤러용) 칭호 조회 -> BadgeQueryService에 위임
    public List<UserBadge> getBadgesByUserId(String userId) {
        return badgeQueryService.getBadgesByUserId(userId);
    }

    // (컨트롤러용) 배치 수동 실행 -> BadgeCalculator에 위임
    public void calculateBadges() {
        badgeCalculator.calculateBadges();
    }
}