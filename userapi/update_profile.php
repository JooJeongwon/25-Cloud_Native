<?php
// 1. 세션 및 DB 연결
include_once("./redis_session.php"); // Redis 세션 시작
include_once("./database.php");    // DB 연결

// 2. 응답 형식 및 초기화
header('Content-Type: application/json');
$ret = array();

// 3. [보안] 로그인 상태 확인
if (!isset($_SESSION['user_id'])) {
    $ret['result'] = "no";
    $ret['msg'] = "로그인이 필요합니다";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}

// 4. 입력값 유효성 검사
if (empty($_POST['name'])) {
    $ret['result'] = "no";
    $ret['msg'] = "새 이름 정보가 없음";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}

// 5. 변수 할당
$new_name = $_POST['name'];
$user_id = $_SESSION['user_id'];

try {
    // 6. [DB 업데이트] user 테이블의 name 변경
    $query = "UPDATE user SET name = :name WHERE id = :id";
    $stmt = $pdo->prepare($query);
    $stmt->execute([
        'name' => $new_name,
        'id' => $user_id
    ]);

    // 7. [세션 동기화] DB가 변경됐으므로, 세션 정보도 갱신
    $_SESSION['username'] = $new_name;

    $ret['result'] = "ok";
    $ret['msg'] = "회원 정보가 수정되었음";

} catch (\PDOException $e) {
    // 8. DB 오류 처리
    $ret['result'] = "no";
    $ret['msg'] = "데이터베이스 오류: " . $e->getMessage();
}

// 9. 최종 결과 출력
echo json_encode($ret, JSON_UNESCAPED_UNICODE);
?>