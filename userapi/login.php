<?php
include_once("./redis_session.php"); // Redis 세션 시작

// 데이터베이스 연결 파일 포함 (PDO 사용)
include_once("./database.php");

// 모든 응답은 JSON 형식으로 통일
header('Content-Type: application/json');
$ret = array(); // 응답으로 보낼 배열 초기화

/*
// --- [경고] 개발 테스트용 GET 방식 허용 코드 ---
// 이 부분의 주석을 풀면 URL(?email=...&password=...)로 로그인을 테스트할 수 있음
// 테스트 완료 후, 보안을 위해 반드시 다시 주석 처리할 것
if (!empty($_GET['email']) && !empty($_GET['password'])) {
    $_POST['email'] = $_GET['email'];
    $_POST['password'] = $_GET['password'];
}
// --- 테스트용 코드 끝 ---
*/

// POST 데이터 유효성 검사
if (empty($_POST['email'])) {
    $ret['result'] = "no";
    $ret['msg'] = "이메일 정보가 없음";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}
if (empty($_POST['password'])) {
    $ret['result'] = "no";
    $ret['msg'] = "패스워드 정보가 없음";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}

$email = $_POST['email'];
$password = $_POST['password'];

try {
    // 이메일로 사용자 정보 조회 (SQL 쿼리)
    $query = "SELECT * FROM user WHERE email = :email";
    $stmt = $pdo->prepare($query);
    $stmt->execute(['email' => $email]);
    $row = $stmt->fetch();

    // [보안] password_verify로 해시된 비밀번호 검증
    if (!$row || !password_verify($password, $row['password'])) {
        $ret['result'] = "no";
        $ret['msg'] = "로그인 정보가 틀림";
    } else {
        // 로그인 성공

        // [보안] 세션 고정 공격 방지를 위해 세션 ID 갱신
        session_regenerate_id(true);

        // 세션에 사용자 정보 저장
        $_SESSION['user_id'] = $row['id'];
        $_SESSION['username'] = $row['name'];
        $_SESSION['useremail'] = $row['email'];
        $_SESSION['role'] = $row['role'];

        // Java/Python을 위한 '단순 키'를 Redis에 추가 저장
        try {
            // @intelephense-suppress P1009
            $redis = new \Redis();
            // ⭐️ docker-compose.yml의 'redis' 서비스 이름으로 접속
            $redis->connect('redis', 6379);

            $session_id = session_id(); // 갱신된 세션 ID 가져오기

            // Java(reviewapi)를 위한 userId 저장
            $redis->set("session_userid:" . $session_id, $row['id']);
            $redis->expire("session_userid:" . $session_id, 3600); // (선택) 1시간 후 자동 삭제

            // Python(verificationapi)을 위한 role 저장
            $redis->set("session_role:" . $session_id, $row['role']);
            $redis->expire("session_role:" . $session_id, 3600); // (선택) 1시간 후 자동 삭제

            $redis->close();

        } catch (Exception $e) {
            // (선택) Redis 연결 실패 시 로그 남기기
            // error_log("Redis connection failed for simple keys: " . $e->getMessage());
            // Redis에 단순 키 저장을 실패해도, 로그인은 성공으로 처리합니다.
        }
        $ret['user_id'] = $row['id'];
        $ret['role'] = $row['role']; // (예: "admin" 또는 "user")

        $ret['result'] = "ok";
        $ret['msg'] = "정상 로그인이 되었음";
    }

} catch (\PDOException $e) {
    // DB 오류 처리
    $ret['result'] = "no";
    $ret['msg'] = "데이터베이스 오류: " . $e->getMessage();

}

// 최종 결과 출력
echo json_encode($ret, JSON_UNESCAPED_UNICODE);
?>