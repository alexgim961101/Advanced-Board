# 백엔드(Task) 사전과제
## 개발 환경
- 자바 17
- 스프링 부트 3.1.2
- maria DB 10
- redis 7.0.9 -> 캐시 + 동시성 제어 용도
- 도커 20.10.22
- 도커 컴포즈 2.15.1 -> 쉽게 실행시키기 위해서
## 프로젝트 구조 소개
### 1). 프로젝트 구조
![스크린샷 2023-08-20 오후 8.32.02.png](..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202023-08-20%20%EC%98%A4%ED%9B%84%208.32.02.png)
### 2). 테이블 구조
![스크린샷 2023-08-20 오후 3.37.31.png](..%2F..%2F..%2FDesktop%2F%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%202023-08-20%20%EC%98%A4%ED%9B%84%203.37.31.png)
## 구현 방법에 대한 소개
### 1). 게시글에 속한 게시글 목록 조회 API
#### 검색 기능 포함
서비스 로직
```java
@Transactional(readOnly = true)
@Cacheable(cacheNames = "boardDetail", key = "#boardDetailId + ':' + #keyword + ':' + #pageRequest.pageNumber + ':' + #pageRequest.offset", cacheManager = "rcm")
public CustomPage<BoardDetailRespDto> readBySearchPaging(Long boardId, String keyword, PageRequest pageRequest) {
    Page<BoardDetail> boardDetailPageEntity = boardDetailRepository.findAllByBoardIdNameContainingAndStatusOrderById(boardId, keyword, BoardDetailStatus.EXIST, pageRequest);

    CustomPage<BoardDetailRespDto> page = new CustomPage<>();

    for(BoardDetail boardDetail : boardDetailPageEntity.getContent()) {
        BoardDetailRespDto boardDetailGetRespPagingDto = BoardDetailRespDto.fromEntity(boardDetail);
        page.getContents().add(boardDetailGetRespPagingDto);
    }

    page.setPage(boardDetailPageEntity);

    return page;
}
```
쿼리
```java
@Timer
@Query(value = "select d from BoardDetail d join fetch d.board b where d.board.id = :boardId and d.status = :status order by d.id DESC")
Page<BoardDetail> findAllByBoardIdByStatusOrderByIdDesc(Long boardId, BoardDetailStatus status, Pageable pageable);
```
- Redis를 캐시로 이용하여 성능 개선 시도 -> 약 20 ~ 30ms 정도 빨라짐
- @Timer를 이용하여 쿼리 성능 측정
#### 검색 기능 미포함
서비스 로직
```java
@Transactional(readOnly = true)
@Cacheable(cacheNames = "boardDetail", key = "#boardDetailId + ':' + #pageRequest.pageNumber + ':' + #pageRequest.offset", cacheManager = "rcm") public CustomPage<BoardDetailRespDto> readPaging(Long boardId, PageRequest pageRequest) {
    Page<BoardDetail> boardDetailPageEntity = boardDetailRepository.findAllByBoardIdByStatusOrderByIdDesc(boardId, BoardDetailStatus.EXIST, pageRequest);

    CustomPage<BoardDetailRespDto> page = new CustomPage<>();

    for(BoardDetail boardDetail : boardDetailPageEntity.getContent()) {
        BoardDetailRespDto boardDetailGetRespPagingDto = BoardDetailRespDto.fromEntity(boardDetail);
        page.getContents().add(boardDetailGetRespPagingDto);
    }

    page.setPage(boardDetailPageEntity);

    return page;
}
```
쿼리
```java
@Timer
@Query(value = "select d from BoardDetail d join fetch d.board b where d.board.id = :boardId and d.name like %:name% and d.status = :status order by d.id DESC")
Page<BoardDetail> findAllByBoardIdNameContainingAndStatusOrderById(Long boardId, String name, BoardDetailStatus status, Pageable pageable);
```
- 검색 기능 포함이랑 유사하게 구현
### 2). 게시글 단건 조회 API
서비스 로직
```java
@Transactional()
@CacheEvict(cacheNames = "boardDetail", allEntries = true)
public BoardDetailRespDto readOneBoardDetail(Long boardId, Long boardDetailId) {
    BoardDetail boardDetailEntity = boardDetailRepository.findBoardDetail(boardId, boardDetailId).orElseThrow(() -> {
        throw new CustomApiException(NOT_FOUND_BOARD_DETAIL.isSuccess(), NOT_FOUND_BOARD_DETAIL.getMessage());
    });

    if(boardDetailEntity.getStatus() == BoardDetailStatus.DELETE) throw new CustomApiException(ALREADY_DELETED.isSuccess(), ALREADY_DELETED.getMessage());

    RLock lock = redissonClient.getLock(String.valueOf(boardDetailId));
    try {
        lock.tryLock(100, 10, TimeUnit.MILLISECONDS);
        boardDetailEntity.addCount();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    } finally {
        lock.unlock();
    }
    return BoardDetailRespDto.fromEntity(boardDetailEntity);
}
```
- 캐시 적용 X -> 조회수가 계속 변화하는 값이기 때문에 적절하지 않다고 판단
- redis를 이용한 분산락 구현 -> 조회수 동시성 문제 해결

index
```java
@Entity
@Table(name = "board_detail", indexes = {@Index(name = "fk_pk_index", columnList = "board_id,id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BoardDetail extends BaseEntity {
```
- index를 이용 -> 성능 개선
### 3). 게시글 등록 API
서비스 로직
```java
@Transactional
@CacheEvict(cacheNames = "boardDetail", allEntries = true)
public Long saveBoardDetail(Long boardId, String userId, BoardDetailPostReqDto boardDetailPostReqDto) {
    BoardDetail boardDetail = BoardDetail.createBoardDetail(boardDetailPostReqDto.getName(),
        userId,
        boardDetailPostReqDto.getContent());

    Board boardEntity = boardRepository.findById(boardId).orElseThrow(() -> {
        throw new CustomApiException(NOT_FOUND_BOARD.isSuccess(), NOT_FOUND_BOARD.getMessage());
    });
    boardDetail.setBoard(boardEntity);
    BoardDetail boardDetailEntity = boardDetailRepository.save(boardDetail);
    return boardDetailEntity.getId();
}
```
- JPA의 더티 체킹을 이용할 수도 있었지만 출력값으로 게시물의 Id가 필요하기 때문에 사용 X
### 4). 게시글 수정 API
서비스 로직
```java
@Transactional
@CacheEvict(cacheNames = "boardDetail", allEntries = true)
public BoardDetailRespDto updateBoardDetail(Long boardId, Long boardDetailId, String userId, BoardDetailPutReqDto boardDetailPutReqDto) {
    BoardDetail boardDetailEntity = boardDetailRepository.findBoardDetail(boardId, boardDetailId).orElseThrow(() -> {
        throw new CustomApiException(NOT_FOUND_BOARD_DETAIL.isSuccess(), NOT_FOUND_BOARD_DETAIL.getMessage());
    });

    if(boardDetailEntity.getStatus() == BoardDetailStatus.DELETE) throw new CustomApiException(ALREADY_DELETED.isSuccess(), ALREADY_DELETED.getMessage());
    if(!boardDetailEntity.getUsername().equals(userId)) throw new CustomApiException(DO_NOT_MATCH_USERNAME.isSuccess(), DO_NOT_MATCH_USERNAME.getMessage());

    boardDetailPutReqDto.setBoardEntity(boardDetailEntity);
    return BoardDetailRespDto.fromEntity(boardDetailEntity);
}
```
- 이미 지워진 게시물은 수정 불가능
- 작성자 이외의 다른 사람 수정 불가능
### 5). 게시글 삭제 API
서비스 로직
```java
@Transactional
@CacheEvict(cacheNames = "boardDetail", allEntries = true)
public Long deleteBoardDetail(Long boardId, Long boardDetailId, String userId) {
    BoardDetail boardDetailEntity = boardDetailRepository.findBoardDetail(boardId, boardDetailId).orElseThrow(() -> {
        throw new CustomApiException(NOT_FOUND_BOARD_DETAIL.isSuccess(), NOT_FOUND_BOARD_DETAIL.getMessage());
    });

    if(!boardDetailEntity.getUsername().equals(userId)) throw new CustomApiException(DO_NOT_MATCH_USERNAME.isSuccess(), DO_NOT_MATCH_USERNAME.getMessage());
    if(boardDetailEntity.getStatus().equals(BoardDetailStatus.DELETE)) throw new CustomApiException(ALREADY_DELETED.isSuccess(), ALREADY_DELETED.getMessage());
    boardDetailEntity.setStatus(BoardDetailStatus.DELETE);

    return boardDetailEntity.getId();
}
```
- 작성자 이외의 다른 사람은 삭제 불가능
- 이미 지워진 게시물 삭제시 예외 발생
- 완전 삭제가 아니라 상태만 변경
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
### IntelliJ로 실행시킬 경우 Edit Configurations -> Modify Options -> Environment varables에서 .env파일과 동일하게 key-value값 설정 후 실행
## 기타 구현 내용
### - 공통 에러 처리를 위한 핸들러 및 custom 에러
### - 성능 측정을 위한 @Timer
### - 테스트를 쉽게 하기 위한 DummyObject 클래스
## API 예시 (자세한 내용은 mailplug_postman_collection.json 파일 참고)
### http://localhost:8080 <-> http://43.202.142.100:80 사용 가능 (aws 주소는 비용문제 때문에 언제든 멈출 수 있음)
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