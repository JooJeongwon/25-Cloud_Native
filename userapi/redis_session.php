<?php
// [설정] 세션 저장 핸들러를 'redis'로 변경
ini_set('session.save_handler', 'redis');

// [설정] Redis 서버의 주소와 포트 설정
// "redis"는 우리가 띄울 Redis 컨테이너의 이름임
ini_set('session.save_path', 'tcp://redis:6379'); 

// 설정이 완료된 후, 세션 시작
session_start();
?>