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

// 4. 입력값 유효성 검사 (비밀번호 확인)
if (empty($_POST['password'])) {
    $ret['result'] = "no";
    $ret['msg'] = "본인 확인을 위한 비밀번호가 없음";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}

// 5. 변수 할당
$password = $_POST['password'];
$user_id = $_SESSION['user_id'];

try {
    // 6. [본인 인증] DB에서 현재 비밀번호 해시 가져오기
    $query_check = "SELECT password FROM user WHERE id = :id";
    $stmt_check = $pdo->prepare($query_check);
    $stmt_check->execute(['id' => $user_id]);
    $row = $stmt_check->fetch();

    // 7. [보안] 비밀번호 검증
    if (!$row || !password_verify($password, $row['password'])) {
        $ret['result'] = "no";
        $ret['msg'] = "비밀번호가 일치하지 않음";
    } else {
        // 8. [DB 삭제] 비밀번호 일치, 계정 삭제 실행
        $query_delete = "DELETE FROM user WHERE id = :id";
        $stmt_delete = $pdo->prepare($query_delete);
        $stmt_delete->execute(['id' => $user_id]);

        // 9. [세션 파괴] 계정이 삭제됐으므로 강제 로그아웃
        session_unset();
        session_destroy();

        $ret['result'] = "ok";
        $ret['msg'] = "회원 탈퇴가 완료되었음";
    }

} catch (\PDOException $e) {
    // 10. DB 오류 처리
    $ret['result'] = "no";
    $ret['msg'] = "데이터베이스 오류: " . $e->getMessage();
}

// 11. 최종 결과 출력
echo json_encode($ret, JSON_UNESCAPED_UNICODE);
?>