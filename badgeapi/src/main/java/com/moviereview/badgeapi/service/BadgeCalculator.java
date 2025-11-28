package com.moviereview.badgeapi.service;

import com.moviereview.badgeapi.dto.MovieDetailDto;
import com.moviereview.badgeapi.dto.VerificationDto;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BadgeCalculator {

    private final VerificationApiClient verificationApiClient;
    private final MovieApiClient movieApiClient;
    private final BadgeProcessor badgeProcessor;

    public BadgeCalculator(VerificationApiClient verificationApiClient, MovieApiClient movieApiClient,
            BadgeProcessor badgeProcessor) {
        this.verificationApiClient = verificationApiClient;
        this.movieApiClient = movieApiClient;
        this.badgeProcessor = badgeProcessor;
    }

    /**
     * @Async: 이 메소드를 별도 스레드에서 실행
     */
    @Async
    @Scheduled(cron = "0 0 4 * * *")
    public void calculateBadges() {
        System.out.println("칭호 계산 배치를 시작합니다...");

        // --- 1단계: 데이터 수집 (네트워크 IO) ---
        List<VerificationDto> allVerifications = verificationApiClient.fetchAllVerifiedWatches();
        if (allVerifications == null || allVerifications.isEmpty()) {
            System.out.println("승인된 시청 기록이 없습니다. 칭호 계산을 종료합니다.");
            return;
        }
        System.out.println("총 " + allVerifications.size() + "개의 승인된 기록을 가져왔습니다.");

        Set<Integer> movieIds = allVerifications.stream()
                .map(VerificationDto::getMovieId)
                .collect(Collectors.toSet());

        Map<Integer, MovieDetailDto> movieDetailsMap = movieIds.stream()
                .collect(Collectors.toMap(
                        movieId -> movieId,
                        movieApiClient::getMovieDetail // 캐시된 MovieApiClient 호출
                ));
        System.out.println("총 " + movieDetailsMap.size() + "개의 영화 장르 정보를 로드했습니다.");

        // --- 2단계: DB 처리 (트랜잭션 IO) ---
        badgeProcessor.analyzeAndSaveBadges(allVerifications, movieDetailsMap);

        System.out.println("칭호 계산 배치를 완료했습니다.");
    }
}