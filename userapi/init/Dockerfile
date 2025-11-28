# 1. MySQL 공식 이미지 사용
FROM mysql:8.2

# 2. 스키마 파일을 컨테이너의 자동 실행 폴더로 복사
COPY schema.sql /docker-entrypoint-initdb.d/
