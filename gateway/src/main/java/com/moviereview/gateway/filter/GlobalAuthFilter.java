package com.moviereview.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class GlobalAuthFilter implements GlobalFilter, Ordered {

    private final ReactiveStringRedisTemplate redisTemplate;

    public GlobalAuthFilter(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (!path.startsWith("/api/")) {
            return chain.filter(exchange);
        }

        if (path.startsWith("/api/auth/") || path.startsWith("/api/movies/") || path.startsWith("/api/uploads/")) {
            return chain.filter(exchange);
        }

        HttpCookie sessionCookie = request.getCookies().getFirst("PHPSESSID");
        if (sessionCookie == null) {
            System.out.println("❌ [Gateway] 쿠키 없음: " + path);
            // 쿠키가 없어도 빈 헤더를 추가해서 500 오류 방지
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", "")
                    .header("X-User-Role", "")
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        }

        String sessionId = sessionCookie.getValue();

        // Redis 조회
        Mono<String> userIdMono = redisTemplate.opsForValue().get("session_userid:" + sessionId);

        // roleMono는 '선택적'으로 변경. 없으면 "guest"
        Mono<String> roleMono = redisTemplate.opsForValue().get("session_role:" + sessionId)
                .defaultIfEmpty("guest"); // ⭐️ 경합 상태 방지

        // Mono.zip(A, B) -> A.zipWith(B)
        // userIdMono가 성공해야만 flatMap이 실행됨
        return userIdMono.zipWith(roleMono)
                .flatMap(tuple -> {
                    String userId = tuple.getT1(); // (userIdMono가 비어있으면 이 flatMap 자체가 실행 안 됨)
                    String role = tuple.getT2(); // (roleMono가 비어있으면 "guest")

                    System.out.println("✅ [Gateway] 인증 성공 - UserID: " + userId + ", Role: " + role);

                    ServerHttpRequest.Builder requestBuilder = request.mutate()
                            .header("X-User-Id", userId)
                            .header("X-User-Role", role);

                    // 동적으로 경로를 재작성하여 라우팅
                    if (path.equals("/api/reviews/my-reviews")) {
                        URI newUri = UriComponentsBuilder.fromUri(request.getURI())
                                .replacePath("api/reviews/user/" + userId)
                                .build(true)
                                .toUri();
                        requestBuilder.uri(newUri);
                        System.out.println("🔀 [Gateway] 경로 변경: " + path + " -> " + newUri);
                    } else if (path.equals("/api/badges/my-badges")) {
                        URI newUri = UriComponentsBuilder.fromUri(request.getURI())
                                .replacePath("api/badges/user/" + userId)
                                .build(true)
                                .toUri();
                        requestBuilder.uri(newUri);
                        System.out.println("🔀 [Gateway] 경로 변경: " + path + " -> " + newUri);
                    }

                    return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
                })
                .switchIfEmpty(Mono.defer(() -> { // 이 블록은 'userId'가 없을 때만 실행됨
                    // 세션이 없어도 빈 헤더를 추가해서 500 오류 방지
                    ServerHttpRequest mutatedRequest = request.mutate()
                            .header("X-User-Id", "")
                            .header("X-User-Role", "")
                            .build();
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                }));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}