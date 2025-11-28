package com.moviereview.badgeapi.service;

import com.moviereview.badgeapi.dto.MovieDetailDto;
import com.moviereview.badgeapi.dto.VerificationDto;
import com.moviereview.badgeapi.dto.GenreDto;
import com.moviereview.badgeapi.entity.UserBadge;
import com.moviereview.badgeapi.repository.UserBadgeRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Component
public class BadgeProcessor {

    private final UserBadgeRepository userBadgeRepository;

    public BadgeProcessor(UserBadgeRepository userBadgeRepository) {
        this.userBadgeRepository = userBadgeRepository;
    }

    /**
     * DB에 저장/분석하는 로직만 별도의 public 메소드로 분리
     * 
     * @Transactional: 이 메소드에서만 트랜잭션이 실행
     * @CacheEvict: 이 작업(DB 변경)이 성공하면 'badges' 캐시를 삭제
     */
    @Transactional
    @CacheEvict(value = "badges", allEntries = true)
    public void analyzeAndSaveBadges(List<VerificationDto> allVerifications,
            Map<Integer, MovieDetailDto> movieDetailsMap) {

        System.out.println("데이터 분석 및 DB 저장 트랜잭션 시작...");

        Map<String, Map<String, Long>> userGenreCounts = allVerifications.stream()
                .collect(Collectors.groupingBy(
                        VerificationDto::getUserId,
                        Collectors.flatMapping(
                                v -> {
                                    MovieDetailDto details = movieDetailsMap.get(v.getMovieId());
                                    if (details == null || details.getGenres() == null) {
                                        return java.util.stream.Stream.empty();
                                    }
                                    return details.getGenres().stream().map(GenreDto::getName);
                                },
                                Collectors.groupingBy(genreName -> genreName, Collectors.counting()))));

        System.out.println("칭호 부여 로직을 시작합니다...");

        userGenreCounts.forEach((userId, genreCounts) -> {
            long dramaCount = genreCounts.getOrDefault("드라마", 0L);

            // (테스트용) 1편 이상으로 조건 설정
            if (dramaCount >= 1) {
                String badgeName = "DRAMA_LOVER"; // 칭호 이름

                // DB에 이 칭호가 이미 있는지 확인
                Optional<UserBadge> existingBadge = userBadgeRepository.findByUserIdAndBadgeName(userId, badgeName);

                // 칭호가 없을 때(isEmpty)만 저장
                if (existingBadge.isEmpty()) {
                    UserBadge newBadge = new UserBadge();
                    newBadge.setUserId(userId);
                    newBadge.setBadgeName(badgeName);
                    newBadge.setDescription("드라마 장르의 영화 1편 이상 관람");
                    userBadgeRepository.save(newBadge);
                    System.out.println(userId + "에게 '" + badgeName + "' 칭호 부여!");
                } else {
                    System.out.println(userId + "는(은) 이미 '" + badgeName + "' 칭호를 가지고 있습니다.");
                }
            }

            // 공포 칭호 로직 (1편 이상)
            long horrorCount = genreCounts.getOrDefault("공포", 0L); // "공포" 장르 확인

            if (horrorCount >= 1) { // 1편 이상 봤다면
                String badgeName = "HORROR_EXPERT"; // 칭호 이름

                // 중복 확인
                Optional<UserBadge> existingBadge = userBadgeRepository.findByUserIdAndBadgeName(userId, badgeName);

                if (existingBadge.isEmpty()) {
                    UserBadge newBadge = new UserBadge();
                    newBadge.setUserId(userId);
                    newBadge.setBadgeName(badgeName);
                    newBadge.setDescription("공포 영화 1편 이상 관람"); // 설명
                    userBadgeRepository.save(newBadge); // DB 저장
                    System.out.println(userId + "에게 '" + badgeName + "' 칭호 부여!");
                } else {
                    System.out.println(userId + "는(은) 이미 '" + badgeName + "' 칭호를 가지고 있습니다.");
                }
            }
        });

        System.out.println("DB 저장 트랜잭션 완료.");
    }
}