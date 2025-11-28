<?php
include_once("./redis_session.php"); // Redis 세션 시작

error_log("Logout called for session: " . session_id());

// 1. 세션 변수 모두 제거
session_unset();

// 2. 세션 완전 파기 (서버 측)
session_destroy();

// 모든 응답은 JSON 형식으로 통일
header('Content-Type: application/json');

// 응답 배열 생성
$response = array(
    'result' => 'ok',
    'msg' => '로그아웃 되었습니다'
);

// JSON 형식으로 응답
echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>