# 1. PHP 8.2 Apache 이미지 사용
FROM php:8.2-apache

# 2. MySQL 드라이버(pdo_mysql)와 Redis 드라이버(redis) 설치
RUN apt-get update && apt-get install -y libhiredis-dev \
    && pecl install redis \
    && docker-php-ext-enable redis \
    && docker-php-ext-install pdo pdo_mysql \
    && rm -rf /var/lib/apt/lists/*

# 3. 소스 코드 복사
COPY . /var/www/html/