# 백엔드(Task) 사전과제
## 개발 환경
- 자바 17
- 스프링 부트 3.1.2
- maria DB 10
- redis 7.0.9
- 도커 20.10.22
- 도커 컴포즈 2.15.1
## 프로젝트 구조 소개
### 1). 프로젝트 구조
### 2). 테이블 구조
![스크린샷 2023-08-20 오후 3.37.31.png](..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202023-08-20%20%EC%98%A4%ED%9B%84%203.37.31.png)
## 구현 방법에 대한 소개
### 1). 게시글에 속한 게시글 목록 조회 API
### 2). 게시글 단건 조회 API
### 3). 게시글 등록 API
### 4). 게시글 수정 API
### 5). 게시글 삭제 API
## 빌드 & 실행 방법
### 1). 도커 데스크탑(도커 + 도커 컴포즈) 설치
### 2). mailplug-test 바로 하위 경로에서 프로젝트 빌드 (gradlew파일과 동일한 위치)
```bash
./gradlew build
```
### 아래의 명령어 실행
```bash
docker-compose up
```
## API 예시
### 1). 게시글 생성 GET http://localhost:8080/api/board/{boardId}
#### URL 예시 : GET  http://localhost:8080/api/board/1
#### - Request
헤더
```text
X-USERID : alexgim
```
바디
```json
{
  "name" : "name",
  "content" : "content"
}
```
#### - Response
```json
{
    "code": 201,
    "message": "성공적으로 게시물을 생성했습니다",
    "data": 1
}
```
### 2). 게시글 단건 조회 GET http://localhost:8080/api/board/{boardId}/{boardDetailId}
#### URL 예시 GET http://localhost:8080/api/board/1/1
#### - Response
```json
{
  "code": 200,
  "message": "성공적으로 게시물을 읽어왔습니다",
  "data": {
    "id": 1,
    "title": "title",
    "content": "content",
    "username": "alexgim",
    "count": 1,
    "createAt": "23-08-20",
    "updatedAt": "23-08-20"
  }
}
```
### 3). 게시글 수정 PUT http://localhost:8080/api/board/{boardId}/{boardDetailId}
#### URL 예시 : http://localhost:8080/api/board/1/1
#### - Request
헤더
```text
X-USERID : alexgim
```
바디
```json
{
  "name" : "changeName",
  "content" : "changeContent"
}
```
#### - Response
```json
{
  "code": 201,
  "message": "성공적으로 게시물을 수정했습니다",
  "data": {
    "id": 1,
    "title": "changeName",
    "content": "changeContent",
    "username": "alexgim",
    "count": 1,
    "createAt": "23-08-20",
    "updatedAt": "23-08-20"
  }
}
```
### 4). 게시글 삭제 DELETE http://localhost:8080/api/board/{boardId}/{boardDetailId}
#### URL 예시 : DELETE  http://localhost:8080/api/board/1/1
#### - Request
헤더
```text
X-USERID : alexgim
```
#### - Response
```json
{
  "code": 201,
  "message": "성공적으로 게시물을 삭제했습니다",
  "data": 1
}
```
### 5). 게시글 목록 조회 (페이징, 검색 X) GET http://localhost:8080/api/board/1?
#### URL 예시 : GET http://localhost:8080/api/board/1?page=1&limit=3&search=
#### - Request
params
```text
page : 1
limit : 3
```
#### - Response
```json
{
  "code": 200,
  "message": "게시물을 불러왔습니다",
  "data": {
    "contents": [
      {
        "id": 6,
        "title": "name6",
        "content": "content6",
        "username": "alexgim",
        "count": 0,
        "createAt": "23-08-20",
        "updatedAt": "23-08-20"
      },
      {
        "id": 5,
        "title": "name5",
        "content": "content5",
        "username": "alexgim",
        "count": 0,
        "createAt": "23-08-20",
        "updatedAt": "23-08-20"
      },
      {
        "id": 4,
        "title": "name4",
        "content": "content4",
        "username": "alexgim",
        "count": 0,
        "createAt": "23-08-20",
        "updatedAt": "23-08-20"
      }
    ],
    "totalPages": 2,
    "totalElements": 5,
    "last": false,
    "size": 3,
    "number": 0,
    "numberOfElements": 3,
    "first": true,
    "empty": false
  }
}
```
### 6). 게시글 목록 조회 (페이징, 검색 O) GET http://localhost:8080/api/board/1?
#### URL 예시 : GET http://localhost:8080/api/board/1?page=1&limit=3&search=na
#### - Request
params
```text
page : 1
limit : 3
search : na
```
#### - Response
```json
{
  "code": 200,
  "message": "게시물을 불러왔습니다",
  "data": {
    "contents": [
      {
        "id": 6,
        "title": "name6",
        "content": "content6",
        "username": "alexgim",
        "count": 0,
        "createAt": "23-08-20",
        "updatedAt": "23-08-20"
      },
      {
        "id": 5,
        "title": "name5",
        "content": "content5",
        "username": "alexgim",
        "count": 0,
        "createAt": "23-08-20",
        "updatedAt": "23-08-20"
      },
      {
        "id": 4,
        "title": "name4",
        "content": "content4",
        "username": "alexgim",
        "count": 0,
        "createAt": "23-08-20",
        "updatedAt": "23-08-20"
      }
    ],
    "totalPages": 2,
    "totalElements": 5,
    "last": false,
    "size": 3,
    "number": 0,
    "numberOfElements": 3,
    "first": true,
    "empty": false
  }
}
```