# 1. 빌드 단계
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN gradle build --no-daemon -x test 
# (-x test: 테스트 코드 실행 건너뛰기 - 빌드 속도 향상)

# 2. 실행 단계
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]