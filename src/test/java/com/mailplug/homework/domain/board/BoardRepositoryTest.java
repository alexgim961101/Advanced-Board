package com.mailplug.homework.domain.board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BoardRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BoardRepository boardRepository;

    @Test
    @DisplayName(value = "게시판 저장 확인")
    public void saveTest() {
        // given
        Board board = Board.createBoard("title");

        // when
        Board boardEntity = em.persist(board);

        // then
        assertEquals(board.getTitle(), boardEntity.getTitle());
    }

    @Test
    @DisplayName(value = "게시판 불러오기")
    public void findBoardTest() {
        // given
        Board board = Board.createBoard("title");
        Board boardEntity = boardRepository.save(board);

        // when
        Board findBoardEntity = boardRepository.findById(boardEntity.getId()).get();

        // then
        assertEquals(board.getId(), findBoardEntity.getId());
    }

}