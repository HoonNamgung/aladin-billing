# ✅ 사용한 AI 프롬프트 정리 (used_prompts.md)

## 1. 사용자 API 구현 관련
```
당신은 첨부파일의 과제를 수행하는 시니어 백엔드 개발자입니다.
우선 첫번째 사용자 관련 API 6개를 구현하는데,
users 도메인 아래에 controller, service, repository 3가지 layer로 나누어서 작성해주시고
코틀린을 사용할 것이기 때문에 build.gradle.kts에는 부트 3버전을 베이스로
SQLite3, JWT 인증, 테스트 코드, API 구현을 위한 플러그인들을 포함하여
프로젝트 전체를 구현해주세요
상세 구현 내용은 첨부파일을 참고합니다
```

## 2. JWT 인증 + AOP 방식 + 코틀린스럽게
```
소셜로그인 구현 및 관련 API 작성,
로그인시 JWT 표준을 기반으로 생성된 access_token을 반환하고
회원가입을 제외한 다른 요청들은 Authorization: Bearer <token>
헤더가 필요하고 토큰을 통해 유저 정보를 가져올 수 있는 기능과
토큰 없이 접근시 401, 유효하지 않은 ID로 접근하는 경우 404를 반환할 수 있도록
AOP 관점에서 시니어 개발자다운 더 나은 솔루션으로 코틀린스럽게 구현해주세요
```

## 3. Google OAuth2 설정 방식 명시
```
구글 소셜 로그인을 해주실거면 GoogleHttpClient 라는 이름으로 해주시고
GoogleProperties 에서 ConfigurationProperties 어노테이션으로
url처럼 연동할 때 필요한 정보들을 가져올 수 있게 해주세요
```

## 4. README 전체 통합 요청
```
readme 먼저 만들어주세요.
실행 방법 설명을 위한 api 명세, 테스트용 샘플 데이터와 간단한 설명이 있어야 하고
readme 마크다운 양식에 맞춰서 적어주세요.
readme 파일과는 별개로 sqlite 초기화 스크립트,
JWT 발급 및 검증 흐름 다이어그램
다이어그램에 OAuth2 흐름도 상세하게 적어주세요
```

## 5. 중간 디버깅
```
No default constructor for entity : com.example.api.users.model.User
```

## 6. TODO 생성 요청
```
이제 todo 관련 내용도 구현해야해요
당신은 시니어 개발자로 코틀린스럽게 과제의 todo 관련 6가지 api를 구현해야 합니다
sqlite 관련 내용, jwt 토큰이 필요하다는 것, readme에 적을 내용도 포함해서 전부 다 작성해주세요
```

## 7. README 추가 요청
```
### 4. 수정

http
PUT /users/me
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MDcxNDI4LCJleHAiOjE3NDcwNzUwMjh9.Z0O3w8l4SJKteWfEtLvj-ECoCX3OsKI_zY41jk1q-kI

{
  "nickname": "tester2",
  "password": "pass321"
}


이런식으로 샘플데이터도 만들어주세요
```

## 8. 테이블 초기화 쿼리 요청
```
todo까지 포함해서 테이블 초기화 쿼리 만들어주세요
초기화니까 drop까지요
```

## 9. todo/search 구현
```
GET /todos/search
toggle api는 없고 조건에 맞는 todo 조회하기를 구현해야해요

---------
조건을 jpa에서 처리하게 하는건 별로인가요
```

## 10. 테스트 코드 추가
```
controller, service, client에 대한 테스트 코드를 작성해야 합니다
커버리지를 신경써야 해요 
필수 테스트 대상
* 회원가입->로그인->JWT 인증 흐름 테스트
* TODO 생성->목록조회->수정->삭제 테스트
* JWT 없이 접근시 401 응답
*유효하지 않은 ID로 접근시 404 확인
(controller, service, client 첨부)

--------------------
(테스트 코드 no matching in any candidates test task 이슈로 변경 작업 몇가지 진행)
jupiter를 5.7.0으로 내렸습니다
그래서 그런지 BDDMockito는 못써요
컨트롤러랑 통합 테스트쪽을 좀 바꿔주세요
```

## 11. TC 작성 중 JWT 없는 경우 401 처리 관련 이슈 확인
```
이렇게URL로 구분하고 있으니까 
JwtAuthenticationFilter
에서는 필터가 없는지 체크해서 401 THROW해도 괜찮나요
addFilterBefore가 문제는 안될까요
(SecurityConfig 첨부)
```

## 12. JWT, OAuth2 흐름 이미지 요청
```
> 다이어그램 이미지(auth_flow.png)는 /docs 디렉토리에 포함될 수 있습니다.

readme 기반으로 다이어그램 이미지 auth_flow 만들어주세요

--------------------
mermaid 용 플로우차트 코드로 주세요
```