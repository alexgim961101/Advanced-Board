package com.mailplug.homework.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailplug.homework.domain.board.Board;
import com.mailplug.homework.domain.board.BoardRepository;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import com.mailplug.homework.domain.boardDetail.BoardDetailRepository;
import com.mailplug.homework.domain.boardDetail.BoardDetailStatus;
import com.mailplug.homework.util.DummyObject;
import com.mailplug.homework.web.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BoardDetailServiceTest extends DummyObject {

    // Mock -> InjectMock으로 주입
    @InjectMocks
    private BoardDetailService boardDetailService;
    @Mock
    private BoardDetailRepository boardDetailRepository;
    @Mock
    private BoardRepository boardRepository ;

    @Spy // 진짜 객체를 InjectMocks에 주입
    private ObjectMapper om;

    @Test
    @DisplayName("게시글 생성")
    public void saveBoardDetailTest() throws JsonProcessingException {
        // given
        final BoardDetailPostReqDto dto = BoardDetailPostReqDto.builder()
                .name("title")
                .content("content")
                .build();
        final String userId = "alexgim";

        // stub1
        Board board = newMockBoard(1L, "title", null);
        when(boardRepository.findById(any())).thenReturn(Optional.of(board));

        // stub2
        BoardDetail boardDetail = newMockBoardDetail(1L, "post", userId, "content", board, LocalDateTime.now(), LocalDateTime.now());
        when(boardDetailRepository.save(any())).thenReturn(boardDetail);

        // when
        Long id = boardDetailService.saveBoardDetail(board.getId(), userId, dto);

        // then
        assertEquals(id, 1L);
    }

    @Test
    @DisplayName("게시물 삭제")
    public void deleteBoardDetailTest() {
        // given
        final Long boardId = 1L;
        final Long boardDetailId = 1L;
        final String userId = "alexgim";

        // stub
        Board board = newMockBoard(boardId, "title", null);
        BoardDetail boardDetail = newMockBoardDetail(boardDetailId, "title", userId, "content", board, LocalDateTime.now(), LocalDateTime.now());
        when(boardDetailRepository.findBoardDetail(any(), any())).thenReturn(Optional.of(boardDetail));

        // when
        Long id = boardDetailService.deleteBoardDetail(boardId, boardDetailId, userId);

        // then
        assertEquals(id, boardDetailId);
        assertEquals(boardDetail.getStatus(), BoardDetailStatus.DELETE);
    }

    @Test
    @DisplayName("게시물 조회 (페이징)")
    public void readPaging() {
        // given
        final Long boardId = 1L;
        final PageRequest pageRequest = PageRequest.of(0, 10);
        List<BoardDetail> boardDetails = new ArrayList<>();
        for(int i = 1; i <= 15; i++) {
            boardDetails.add(newMockBoardDetail((long)i, "name" + i, "user" + i, "content", null, LocalDateTime.now(), LocalDateTime.now()));
        }

        // stub
        Page<BoardDetail> boardDetailPage = newMockPageBoardDetail(boardDetails, pageRequest, (long) boardDetails.size());
        when(boardDetailRepository.findAllByBoardIdByStatusOrderByIdDesc(boardId, BoardDetailStatus.EXIST, pageRequest)).thenReturn(boardDetailPage);

        // when
        CustomPage<BoardDetailRespDto> customPage = boardDetailService.readPaging(boardId, pageRequest);

        // then
        assertEquals(customPage.getSize(), 10L);
        assertEquals(customPage.isFirst(), true);
        assertEquals(customPage.getTotalPages(), 2);
    }

    @Test
    @DisplayName("게시물 조회(검색 + 페이징)")
    public void readBySearchPagingTest() {
        // given
        final Long boardId = 1L;
        final String keyword = "사과";
        final PageRequest pageRequest = PageRequest.of(0, 10);
        List<BoardDetail> boardDetails = new ArrayList<>();
        for(int i = 1; i <= 10; i++) {
            boardDetails.add(newMockBoardDetail((long)i, i + 1 + "사과" + i, "user" + i, "content", null, LocalDateTime.now(), LocalDateTime.now()));
        }

        // stub
        Page<BoardDetail> boardDetailPage = newMockPageBoardDetail(boardDetails, pageRequest, (long) boardDetails.size());
        when(boardDetailRepository.findAllByBoardIdNameContainingAndStatusOrderById(any(), any(), any(), any())).thenReturn(boardDetailPage);

        // when
        CustomPage<BoardDetailRespDto> customPage = boardDetailService.readBySearchPaging(boardId, keyword, pageRequest);

        // then
        assertEquals(customPage.getSize(), 10);
        assertEquals(customPage.getContents().get(9).getId(), 10L);
        assertEquals(customPage.getTotalPages(), 1L);
    }

    /**
     * 동시성 제어를 뒤늦게 추가하여 테스트 미흡
     * */
//    @Test
//    @DisplayName("게시물 단건 조회")
//    public void readOneBoardDetailTest() {
//        // given
//        final Long boardId = 1L;
//        final Long boardDetailId = 1L;
//
//        // stub
//        BoardDetail boardDetail = newMockBoardDetail(boardDetailId, "name", "username", "content", null, LocalDateTime.now(), LocalDateTime.now());
//        when(boardDetailRepository.findBoardDetail(any(), any())).thenReturn(Optional.of(boardDetail));
//
//        // when
//        BoardDetailRespDto result = boardDetailService.readOneBoardDetail(boardId, boardDetailId);
//
//        // then
//        assertEquals(result.getCount(), 1L);
//        assertEquals(result.getId(), boardDetailId);
//    }

    @Test
    @DisplayName("게시물 수정")
    public void updateBoardDetail() {
        // given
        final Long boardId = 1L;
        final Long boardDetailId = 1L;
        final String userId = "alex";
        final BoardDetailPutReqDto boardDetailPutReqDto = BoardDetailPutReqDto.builder()
                                                                                .name("title")
                                                                                .content("content").build();

        // stub
        BoardDetail boardDetail = newMockBoardDetail(boardDetailId, "name", userId, "content1234", null, LocalDateTime.now(), LocalDateTime.now());
        when(boardDetailRepository.findBoardDetail(any(), any())).thenReturn(Optional.of(boardDetail));

        // when
        BoardDetailRespDto result = boardDetailService.updateBoardDetail(boardId, boardDetailId, userId, boardDetailPutReqDto);

        // then
        assertEquals(result.getId(), boardDetailId);
    }
}