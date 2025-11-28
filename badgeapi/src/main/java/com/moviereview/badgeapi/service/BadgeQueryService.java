package com.moviereview.badgeapi.service;

import com.moviereview.badgeapi.entity.UserBadge;
import com.moviereview.badgeapi.repository.UserBadgeRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BadgeQueryService {

    private final UserBadgeRepository userBadgeRepository;

    public BadgeQueryService(UserBadgeRepository userBadgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
    }

    @Cacheable(value = "badges", key = "#userId")
    public List<UserBadge> getBadgesByUserId(String userId) {
        System.out.println("DB 조회 (캐시 없음): " + userId);
        return userBadgeRepository.findByUserId(userId);
    }
}