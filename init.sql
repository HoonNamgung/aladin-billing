
-- 기존 테이블 제거
DROP TABLE IF EXISTS todos;
DROP TABLE IF EXISTS users;

-- users 테이블 생성
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    nickname TEXT NOT NULL
);

-- todos 테이블 생성
CREATE TABLE todos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    is_done INTEGER DEFAULT 0, -- 0 = 미완료, 1 = 완료
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 비밀번호는 "password" (BCrypt 암호화됨)
INSERT INTO users (email, password, nickname)
VALUES ('admin@example.com', '$2a$10$WJIrY9A6VjZKy9eH.xN1SOtXzCj.PUdnNblMxXo/NHzI54NbcQa/6', 'admin');

