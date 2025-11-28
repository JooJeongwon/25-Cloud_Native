package com.moviereview.badgeapi.controller;

import com.moviereview.badgeapi.entity.UserBadge;
import com.moviereview.badgeapi.service.BadgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/badges") // http://localhost:8082/badges 로 시작
public class BadgeController {

    // Repository -> Service로 변경
    private final BadgeService badgeService;

    public BadgeController(BadgeService badgeService) {
        this.badgeService = badgeService;
    }

    /**
     * 특정 사용자의 모든 칭호 목록을 조회하는 API
     * (GET http://localhost:8082/badges/user/userA)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBadge>> getBadgesByUserId(@PathVariable String userId) {
        // Repository를 사용해 DB에서 칭호 목록을 조회
        List<UserBadge> badges = badgeService.getBadgesByUserId(userId);
        // 조회된 칭호 목록을 JSON으로 반환
        return ResponseEntity.ok(badges);
    }

    /*
     * (테스트용) 칭호 계산 배치를 "비동기"로 실행
     */
    @GetMapping("/calculate")
    public ResponseEntity<String> calculateBadgesTest() {
        System.out.println("Postman 요청으로 칭호 계산을 '백그라운드'에서 시작합니다...");

        // @Async가 붙은 메소드를 호출 (호출 즉시 리턴됨)
        badgeService.calculateBadges();

        return ResponseEntity.ok("칭호 계산 배치를 백그라운드에서 시작했습니다. (콘솔 로그를 확인하세요)");
    }
}