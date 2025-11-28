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
if (empty($_POST['current_password'])) {
    $ret['result'] = "no";
    $ret['msg'] = "현재 비밀번호 정보가 없음";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}
if (empty($_POST['new_password'])) {
    $ret['result'] = "no";
    $ret['msg'] = "새 비밀번호 정보가 없음";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}

// 5. 변수 할당
$current_password = $_POST['current_password'];
$new_password = $_POST['new_password'];
$user_id = $_SESSION['user_id'];

try {
    // 6. [본인 인증] DB에서 현재 비밀번호 해시 가져오기
    $query_check = "SELECT password FROM user WHERE id = :id";
    $stmt_check = $pdo->prepare($query_check);
    $stmt_check->execute(['id' => $user_id]);
    $row = $stmt_check->fetch();

    // 7. [보안] 현재 비밀번호가 맞는지 검증
    if (!$row || !password_verify($current_password, $row['password'])) {
        $ret['result'] = "no";
        $ret['msg'] = "현재 비밀번호가 일치하지 않음";
    } else {
        // 8. [보안] 새 비밀번호를 해시로 만듦
        $new_password_hash = password_hash($new_password, PASSWORD_DEFAULT);

        // 9. [DB 업데이트] 새 비밀번호 해시로 교체
        $query_update = "UPDATE user SET password = :password WHERE id = :id";
        $stmt_update = $pdo->prepare($query_update);
        $stmt_update->execute([
            'password' => $new_password_hash,
            'id' => $user_id
        ]);

        $ret['result'] = "ok";
        $ret['msg'] = "비밀번호가 성공적으로 변경되었음";
    }

} catch (\PDOException $e) {
    // 10. DB 오류 처리
    $ret['result'] = "no";
    $ret['msg'] = "데이터베이스 오류: " . $e->getMessage();
}

// 11. 최종 결과 출력
echo json_encode($ret, JSON_UNESCAPED_UNICODE);
?>