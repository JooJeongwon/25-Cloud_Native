<?php
$mysql_host = "user-mysql"; // MySQL 서버 호스트 이름
$mysql_user = "user-mysql"; // MySQL 사용자 이름
$mysql_password = "123456"; // MySQL 비밀번호
$mysql_db = "user-mysql"; // 사용할 데이터베이스 이름
$mysql_charset = 'utf8mb4'; // 문자 인코딩 설정

// 데이터베이스 연결(DSN) 정보
$dsn = "mysql:host=$mysql_host;dbname=$mysql_db;charset=$mysql_charset";
$options = [
    PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION, // 오류 발생 시 예외 throw
    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC, // fetch 시 연관 배열로 반환
    PDO::ATTR_EMULATE_PREPARES   => false, // SQL 구문 prepare 지원 활성화
];

try {
    // PDO 인스턴스 생성 및 DB 연결
    $pdo = new PDO($dsn, $mysql_user, $mysql_password, $options);
} catch (\PDOException $e) {
    // 연결 실패 시 예외 처리
    throw new \PDOException($e->getMessage(), (int)$e->getCode());
}
?>