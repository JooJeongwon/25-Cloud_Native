<?php
// 1. 세션 및 DB 연결
include_once("./redis_session.php"); // Redis 세션 시작 (자동 로그인을 위해)
include_once("./database.php");    // DB 연결

// 2. 응답 형식 및 초기화
header('Content-Type: application/json');
$ret = array(); // 응답 배열 초기화

// 3. 입력값 유효성 검사
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
if (empty($_POST['name'])) {
    $ret['result'] = "no";
    $ret['msg'] = "이름 정보가 없음";
    echo json_encode($ret, JSON_UNESCAPED_UNICODE);
    exit;
}

// 4. 입력값 변수 할당
$email = $_POST['email'];
$password = $_POST['password'];
$name = $_POST['name'];

// [role 기능] role 값 받기 (없으면 'user'가 기본값)
$role = 'user'; // 기본값
if (!empty($_POST['role']) && $_POST['role'] == 'admin') {
    $role = 'admin';
}

try {
    // 5. [중복 검사] 이메일이 이미 존재하는지 확인
    $query_check = "SELECT id FROM user WHERE email = :email";
    $stmt_check = $pdo->prepare($query_check);
    $stmt_check->execute(['email' => $email]);

    if ($stmt_check->fetch()) {
        // 이메일이 이미 존재함
        $ret['result'] = "no";
        $ret['msg'] = "이미 사용 중인 이메일";
    } else {
        // 6. [보안] 비밀번호 해시 생성
        $password_hash = password_hash($password, PASSWORD_DEFAULT);

 // 7. [DB 저장] 새 사용자 정보 삽입 (role 추가)
        $query_insert = "INSERT INTO user (email, name, password, role) VALUES (:email, :name, :password, :role)";
        $stmt_insert = $pdo->prepare($query_insert);
        $stmt_insert->execute([
            'email' => $email,
            'name' => $name,
            'password' => $password_hash,
            'role' => $role // role 값 추가     
        ]);

        // 8. [자동 로그인] 방금 가입한 유저의 ID로 세션 생성
        $new_user_id = $pdo->lastInsertId(); // 방금 생성된 user의 id 가져오기
        
        session_regenerate_id(true); // 세션 ID 갱신 (보안)

        $_SESSION['user_id'] = $new_user_id;
        $_SESSION['username'] = $name;
        $_SESSION['useremail'] = $email;
        $_SESSION['role'] = $role; // [V2 기능] 세션에 role 저장

        $ret['result'] = "ok";
        $ret['msg'] = "회원가입이 완료되었음. 자동 로그인됨";
    }

} catch (\PDOException $e) {
    // 9. DB 오류 처리
    $ret['result'] = "no";
    $ret['msg'] = "데이터베이스 오류: " . $e->getMessage();
}

// 10. 최종 결과 출력
echo json_encode($ret, JSON_UNESCAPED_UNICODE);
?>