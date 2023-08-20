package com.mailplug.homework.domain.boardDetail;

import com.mailplug.homework.domain.board.Board;
import com.mailplug.homework.util.DummyObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class BoardDetailRepositoryTest extends DummyObject {

    @Autowired
    private BoardDetailRepository boardDetailRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    /**
     * 게시물 저장
     * 1). 게시물 저장 테스트
     * 2). 게시물 이름이 100자 넘어갔을 때 테스트
     * */
    @Test
    @DisplayName(value = "게시물 저장 테스트")
    public void boardDetailSave() {
        // given
        BoardDetail boardDetail = BoardDetail.createBoardDetail("title", "username", "hello world");

        // when
        BoardDetail boardDetailEntity = testEntityManager.persist(boardDetail);

        // then
        assertEquals(boardDetail.getName(), boardDetailEntity.getName());
    }

    @Test
    @DisplayName(value = "게시물 이름이 100자 넘어갔을 때 테스트")
    public void boardDetailSaveConstraintsStringSize100() {
        // given
        String name = "asdnjaksbdajksbdjkabsdjkabskdjbaskdbjask" +
                "asdjkabsdjkabsjdbaksjbdjkabsdjkabsdjbaksjbfakjsbdjas" +
                "askjdbaksjdbjaksbdjkabskdjabskdjbajksdbajksbdjabskdjabskjdb" +
                "asbdkjabskdjbakjsdbjakbsjdbakjsbdakjsbdkajbsdkjabsdkjabsjdkbasjdk" +
                "ajksbdajsbdkabsdkjabcjk alsnckalskdnaksndlaknsdlkansdkanskdnalksndalksndalk" +
                "ajskbdjakbsdjabskjdbakjsbdajksbdkjabsdjkabsjdbakjsdbakjsbdkajbsdka";
        BoardDetail boardDetail = BoardDetail.createBoardDetail(name, "username", "hello world");

        // when - then
        assertThrows(RuntimeException.class, () -> {
           testEntityManager.persist(boardDetail);
        });


    }


    /**
     * 게시물 상세 불러오기(게시판id, 게시물id)
     * 1). 일반 불러오기 테스트
     * 2). 게시판 id가 존재하지 않는 id인 경우
     * 3). 게시물 id가 존재하지 않는 id인 경우
     * */
    // TODO: 다시 작성하기
    @Test
    @DisplayName(value = "게시글 상세 불러오기 테스트")
    public void readBoardDetailTest() {
        // given
        Board board = Board.createBoard("자유 게시판");
        Board boardEntity = testEntityManager.persist(board);
        System.out.println("--------- [boardId] : " + boardEntity.getId());

        BoardDetail boardDetail = BoardDetail.createBoardDetail("title1", "username1", "content1");

        BoardDetail boardDetailEntity = testEntityManager.persist(boardDetail);
        boardDetailEntity.setBoard(boardEntity);

        // when
        BoardDetail boardDetail1Entity = boardDetailRepository.findBoardDetail(boardEntity.getId(), boardDetailEntity.getId()).get();

        // then
        assertEquals(boardDetail1Entity.getName(), boardDetail.getName());
    }

    @Test
    @DisplayName(value = "게시판 id가 존재하지 않는 id인 경우")
    public void readBoardDetailTestWithoutBoardId() {
        // given
        Board board = Board.createBoard("자유 게시판");
        Board boardEntity = testEntityManager.persist(board);
        System.out.println("--------- [boardId] : " + boardEntity.getId());

        BoardDetail boardDetail = BoardDetail.createBoardDetail("title1", "username1", "content1");
        boardDetail.setBoard(boardEntity);

        // when - then
        assertThrows(NoSuchElementException.class, () -> {
            boardDetailRepository.findBoardDetail(boardEntity.getId() + 1L, 1L).get();
        });
    }

    @Test
    @DisplayName(value = "게시물 id가 존재하지 않는 id인 경우")
    public void readBoardDetailTestWithoutBoardDetailId() {
        // given
        Board board = Board.createBoard("자유 게시판");
        Board boardEntity = testEntityManager.persist(board);
        System.out.println("--------- [boardId] : " + boardEntity.getId());

        BoardDetail boardDetail = BoardDetail.createBoardDetail("title1", "username1", "content1");
        boardDetail.setBoard(boardEntity);

        // when - then
        assertThrows(NoSuchElementException.class, () -> {
            boardDetailRepository.findBoardDetail(boardEntity.getId(), 100L).get();
        });
    }

    /**
     * 게시물 페이징1 (page, limit)
     * 1). 보통
     * 2). page값이 음수인 경우
     * 3). limit 값이 0인 경우
     * */
    @Test
    @DisplayName("게시물 페이징 (page, limit) 테스트")
    public void findAllByBoardIdByStatusOrderByIdDescTest() {
        // given
        // 게시판 생성
        Board board = Board.createBoard("자유 게시판");
        Board boardEntity = testEntityManager.persist(board);
        System.out.println("--------- [boardId] : " + boardEntity.getId());
        // 게시물 생성
        for(int i = 1; i <= 20; i++) {
            BoardDetail mockBoardDetail = newMockBoardDetail(null, "name" + i, "username", "content", boardEntity, LocalDateTime.now(), LocalDateTime.now());
            testEntityManager.persist(mockBoardDetail);
        }

        // Pageable 객체 생성
        PageRequest pageRequest = PageRequest.of(1, 5);

        // when
        Page<BoardDetail> page = boardDetailRepository.findAllByBoardIdByStatusOrderByIdDesc(boardEntity.getId(), BoardDetailStatus.EXIST, pageRequest);

        // then
        assertEquals(page.getTotalElements(), 20L);
        assertEquals(page.getContent().size(), 5);
    }


    /**
     * 게시물 페이징2 (page, limit, search)
     * 1). 보통
     * 2). page값이 음수인 경우
     * 3). limit 값이 0인 경우
     * 4). search=""인 경우
     * */

    @Test
    @DisplayName("게시물 페이징 (page, limit, search) 테스트")
    public void findAllByBoardIdNameContainingAndStatusOrderByIdTest() {
        // given
        // 게시판 생성
        Board board = Board.createBoard("자유 게시판");
        Board boardEntity = testEntityManager.persist(board);
        System.out.println("--------- [boardId] : " + boardEntity.getId());
        // 게시물 생성
        for(int i = 1; i <= 20; i++) {
            BoardDetail mockBoardDetail = newMockBoardDetail(null, "name" + i, "username", "content" + i, boardEntity, LocalDateTime.now(), LocalDateTime.now());
            testEntityManager.persist(mockBoardDetail);
        }

        // Pageable 객체 생성
        PageRequest pageRequest = PageRequest.of(1, 5);

        // when
        Page<BoardDetail> page = boardDetailRepository.findAllByBoardIdNameContainingAndStatusOrderById(boardEntity.getId(), "name", BoardDetailStatus.EXIST, pageRequest);

        // then
        assertEquals(page.getTotalElements(), 20L);
        assertEquals(page.getContent().size(), 5);
    }
}