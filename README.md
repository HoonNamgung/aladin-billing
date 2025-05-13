# ✅ JWT 기반 TODO 백엔드 API (Kotlin + Spring Boot + SQLite)

이 프로젝트는 JWT 기반 인증을 활용한 TODO 백엔드 API입니다.  
Kotlin과 Spring Boot 3, SQLite를 기반으로 구현되었으며, Google OAuth2 기반 소셜 로그인 기능도 포함됩니다.

---

## 📦 프로젝트 개요

- 사용자는 이메일/비밀번호 기반 로그인 또는 Google 소셜 로그인을 통해 인증할 수 있습니다.
- 로그인 시 JWT 토큰을 발급하고, 토큰 기반으로 사용자 인증을 처리합니다.
- JWT 없이 인증이 필요한 API에 접근 시 `401 Unauthorized`, 존재하지 않는 자원 접근 시 `404 Not Found`를 반환합니다.

---

## 🛠️ 실행 방법

### 1. 프로젝트 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

> 실행 후 기본 주소: `http://localhost:8080`

---

### 2. SQLite 초기화 

프로젝트는 SQLite를 데이터베이스로 사용하며, 실행 시 `todo.db` 파일이 생성됩니다.  
초기화의 경우 아래 SQL 파일을 실행할 수 있고 실행시 어드민 계정도 생성됩니다.

```
sqlite3 ./todo.db < init.sql
```

SQLite GUI 툴이나 CLI를 통해 위 파일 SQL을 직접 실행할 수도 있습니다.

---

## 🔐 JWT 인증 흐름

1. 사용자가 `/users/login` 으로 로그인 요청
2. 로그인 성공 시 `access_token` 반환
3. 이후 인증이 필요한 모든 API 호출 시 아래와 같이 JWT 포함:

```
Authorization: Bearer <access_token>
```

4. 서버는 토큰을 검증하여 사용자 인증 처리

---

## 🌐 Google OAuth2 로그인 흐름

1. 클라이언트가 Google 인증 URL로 리다이렉트 (프론트에서 처리)
2. 사용자가 로그인하면 Google이 서버로 `code`를 포함한 콜백 요청
3. 서버는 `/users/social/callback?code=...` 엔드포인트로 해당 code 처리
4. code → access_token → 사용자 정보(userinfo) 요청
5. 이메일 기반으로 회원 가입 또는 로그인 처리
6. JWT 토큰 발급 및 반환

---

## 🔁 인증 흐름 요약

### JWT 로그인 플로우

```
[Client] → POST /users/login
        → [Server] 비밀번호 확인 → JWT 발급
        ← access_token 반환
[Client] → 요청 시 Authorization 헤더에 JWT 포함
        → [Server] JWT 파싱 → userId 추출 → 인증 처리
```

### Google OAuth2 로그인 플로우

```
[Client] → Google 로그인 페이지 접속
        → 로그인 완료 후 Google → 서버에 code 전달
[Server] → /users/social/callback
        → code로 access_token 요청
        → access_token으로 userinfo 요청
        → 이메일 기반 회원 등록 or 로그인
        → JWT 발급 및 반환
```

> 다이어그램 이미지(`auth_flow.png`)는 `/docs` 디렉토리에 포함될 수 있습니다.

---

## ✅ API 명세 요약

### 사용자 관련 API

| 메서드 | 경로                          | 설명               | 인증     |
|--------|-------------------------------|--------------------|----------|
| POST   | `/users/signup`               | 회원가입           | ❌ 없음 |
| POST   | `/users/login`                | 로그인 (JWT 발급)  | ❌ 없음 |
| GET    | `/users/me`                   | 내 정보 조회       | ✅ 필요 |
| PUT    | `/users/me`                   | 내 정보 수정       | ✅ 필요 |
| DELETE | `/users/me`                   | 회원 탈퇴          | ✅ 필요 |
| GET    | `/users/social/callback`      | Google 로그인 처리 | ❌ 없음 |

### TODO 관련 API

| 메서드  | 경로                  | 설명                      | 인증    |
|--------|----------------------|--------------------------|---------|
| POST   | `/todos`             | 할 일 생성                 | ✅ 필요 |
| GET    | `/todos`             | 할 일 전체 조회             | ✅ 필요 |
| GET    | `/todos/{id}`        | 할 일 단건 조회             | ✅ 필요 |
| PUT    | `/todos/{id}`        | 할 일 수정                 | ✅ 필요 |
| PATCH  | `/todos/{id}/toggle` | 할 일 상태 완료/미완료 토글   | ✅ 필요 |
| DELETE | `/todos/{id}`        | 할 일 삭제                 | ✅ 필요 |

> ✅ JWT 인증이 필요한 API는 Authorization 헤더에 토큰을 포함해야 합니다.

---

## 🧾 샘플 요청 데이터

### JWT 사용 예시

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6...
```

### 1. 회원가입

```http
POST /users/signup
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "pass123",
  "nickname": "tester"
}
```

### 2. 로그인

```http
POST /users/login
Content-Type: application/json

{
  "email": "test@example.com",
  "password": "pass123"
}
```

### 3. 조회

```http
GET /users/me
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MDcxNDI4LCJleHAiOjE3NDcwNzUwMjh9.Z0O3w8l4SJKteWfEtLvj-ECoCX3OsKI_zY41jk1q-kI
```

### 4. 수정

```http
PUT /users/me
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MDcxNDI4LCJleHAiOjE3NDcwNzUwMjh9.Z0O3w8l4SJKteWfEtLvj-ECoCX3OsKI_zY41jk1q-kI

{
  "nickname": "tester2",
  "password": "pass321"
}
```

### 5. 삭제

```http
DELETE /users/me
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MDcxNDI4LCJleHAiOjE3NDcwNzUwMjh9.Z0O3w8l4SJKteWfEtLvj-ECoCX3OsKI_zY41jk1q-kI
```

### 6. 소셜 로그인

```
로그인이지만 GPT 초안대로 email이 없으면 계정 생성하는 코드 유지

구글 API 콘솔 (https://console.cloud.google.com/auth/clients)을 통하여 
발급받은 클라이언트 ID와 secret 사용

https://accounts.google.com/o/oauth2/v2/auth?client_id=687905461114-92ob64bca284vemrjhhr1np0gf7qq0ne.apps.googleusercontent.com&redirect_uri=http://localhost:8080/users/social/callback&response_type=code&scope=https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile&access_type=offline
를 통하여 구글로그인 테스트로 callback api 호출 확인 가능

query param 
|*client_id     |687905461114-92ob64bca284vemrjhhr1np0gf7qq0ne.apps.googleusercontent.com                        |
|*redirect_uri  |http://localhost:8080/users/social/callback                                                     |
|*response_type |code                                                                                            |
|*scope         |https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile |
|*access_type   |offline                                                                                         |
```

### 7. 할 일 생성

```http
POST /todos
Content-Type: application/json
Authorization: Bearer {access_token}

{
  "title": "책 읽기"
}
```

### 8. 할 일 전체 조회

```http
GET /todos
Authorization: Bearer {access_token}
```

### 9. 할 일 단건 조회

```http
GET /todos/1
Authorization: Bearer {access_token}
```

### 10. 할 일 수정

```http
PUT /todos/1
Content-Type: application/json
Authorization: Bearer {access_token}

{
  "title": "책 더 열심히 읽기",
  "completed": true
}
```

### 11. DELETE 할 일 삭제

```http
DELETE /todos/1
Authorization: Bearer {access_token}
```

### 12. 할 일 조건 검색

```http
GET /todos/search?isDone=true&keyword=study
Authorization: Bearer {access_token}

query param 
|isDone  | true  |
|keyword | 스터디 |

```

---

## 🧪 테스트

### 테스트 실행

> 각 Controller, Service 케이스별로 대응하도록 하여 
> 테스트 커버리지는 80% 이상을 목표로 합니다.
> 필수 테스트 대상 (401 에러, 403 에러, user 통합 테스트, todo 통합 테스트)
> https://velog.io/@yeseul/No-matching-tests-found-in-any-candidate-test-task.-%EC%97%90%EB%9F%AC-%ED%95%B4%EA%B2%B0
> 해당 이슈로 Settings → Build, Execution, Deployment → Build Tools → Run tests using을 Gradle에서 IntelliJ로 변경하여 실행
```bash
./gradlew test
```


---

## 📁 프로젝트 구조

```
com.example.api
├── config
│   ├── jwt
│   │   ├── JwtAuthenticationFilter.kt
│   │   ├── JwtAuthenticationToken.kt
│   │   ├── JwtProperties.kt
│   │   └── JwtTokenProvider.kt
│   ├─── oauth
│   │   └── GoogleProperties.kt
│   ├─── GlobalExceptionHandler
│   └─── SecurityConfig
├── todos
│   ├── controller
│   │   └── TodoController.kt
│   ├── dto
│   │   └── TodoDto.kt
│   ├── model
│   │   └── Todo.kt
│   ├── service
│   │   └── TodoService.kt
│   ├── exception
│   │   └── TodoException.kt
│   └── repository
│       └── TodoRepository.kt
├── users
│   ├── controller
│   │   ├── SocialLoginController.kt
│   │   └── UserController.kt
│   ├── dto
│   │   ├── GoogleUserInfoResponse.kt
│   │   └── UserDto.kt
│   ├── infra.google.client
│   │   └── GoogleHttpClient.kt
│   ├── model
│   │   └── User.kt
│   ├── repository
│   │   └── UserRepository.kt
│   └── service
│       └── UserService.kt
├── todos (예정)
└── ApiApplication.kt
```

---

## 💬 사용 기술 스택

- Kotlin 1.9
- Spring Boot 3.1.2
- Spring Security
- JWT (`io.jsonwebtoken:jjwt`)
- SQLite3 + Hibernate (JPA)
- Google OAuth2
- Gradle Kotlin DSL
