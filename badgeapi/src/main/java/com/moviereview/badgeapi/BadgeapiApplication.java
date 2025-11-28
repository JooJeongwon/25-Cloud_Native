package com.moviereview.badgeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 스케줄링 기능 활성화 어노테이션 추가
@EnableCaching // 캐싱 기능 활성화 어노테이션 추가
@EnableAsync // 비동기 기능 활성화 어노테이션 추가
public class BadgeapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BadgeapiApplication.class, args);
	}
}