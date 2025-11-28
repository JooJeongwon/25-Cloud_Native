/* DB가 없으면 생성하고, 있으면 사용 */
CREATE DATABASE IF NOT EXISTS `user-mysql`;
USE `user-mysql`;

/* user 테이블 생성 (role 포함) */
CREATE TABLE user (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(50),
  role VARCHAR(50) NOT NULL DEFAULT 'user'  -- 'role' 컬럼 추가 (기본값 'user')
);

/* 테스트용 계정 자동 삽입 (비밀번호 '1234'의 해시값) */
INSERT INTO user (id, email, password, name, role) 
VALUES (1, 'test@test.com', '$2y$10$GQsrx5WmFgCoMDHxIWrmAekiLEFltVrrLNJYokgkMORntr15.V5Fu','test', 'user');

INSERT INTO user (id,vemail, password, name, role) 
VALUES (2, 'admin@admin.com', '$2y$10$GQsrx5WmFgCoMDHxIWrmAekiLEFltVrrLNJYokgkMORntr15.V5Fu', 'admin', 'admin');