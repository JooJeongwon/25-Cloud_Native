package com.moviereview.badgeapi.service;

import com.moviereview.badgeapi.dto.VerificationDto;
import org.springframework.beans.factory.annotation.Value; // @Value import
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import java.util.List;

@Component
public class VerificationApiClient {

    private final WebClient webClient;

    // 하드코딩된 키 삭제
    // private final String INTERNAL_API_KEY = "MY_SUPER_SECRET_MSA_KEY_12345";

    // 설정 파일에서 주입받을 필드
    private final String internalApiKey;

    public VerificationApiClient(WebClient webClient,
            @Value("${api.keys.internal}") String internalApiKey) {
        this.webClient = webClient;
        this.internalApiKey = internalApiKey;
    }

    // .cookie() -> .header()
    public List<VerificationDto> fetchAllVerifiedWatches() {
        System.out.println("verificationapi 호출 중... (API 키 사용)");
        Flux<VerificationDto> verificationFlux = webClient.get()
                .uri("http://verificationapi:8000/api/admin/all-verified")

                // 쿠키 방식 제거
                // .cookie("PHPSESSID", "YOUR_ADMIN_COOKIE_VALUE")

                // 하드코딩된 키 대신 주입받은 'internalApiKey' 사용
                .header("X-API-KEY", this.internalApiKey)

                .retrieve()
                .bodyToFlux(VerificationDto.class);

        return verificationFlux.collectList().block();
    }
}