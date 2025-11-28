<?php
include_once("./redis_session.php"); // Redis 세션 시작

// 모든 응답은 JSON 형식으로 통일
header('Content-Type: application/json');
$ret = array(); // 응답 배열 초기화

// 세션에 사용자 이름이 설정되어 있는지 확인
if (isset($_SESSION['username'])) {
    $ret['result'] = "ok";
    $ret['username'] = $_SESSION['username']; // 세션에서 사용자 이름 가져옴
    $ret['role'] = $_SESSION['role']; // [V2 기능] 현재 로그인된 유저의 role 반환
} else {
    $ret['result'] = "no";
    $ret['msg'] = "로그인이 필요합니다"; // 로그인 필요 메시지
}

// JSON 형식으로 응답
echo json_encode($ret, JSON_UNESCAPED_UNICODE);
?>