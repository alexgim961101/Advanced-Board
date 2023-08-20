package com.mailplug.homework.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mailplug.homework.domain.board.Board;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import com.mailplug.homework.service.BoardDetailService;
import com.mailplug.homework.util.DummyObject;
import com.mailplug.homework.web.dto.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class BoardControllerTest extends DummyObject {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockBean
    private BoardDetailService boardDetailService;


    @Test
    @DisplayName("게시글 등록 API")
    public void saveBoardDetailTest() throws Exception {
        // given
        final Long boardId = 1L;
        final String url = "/api/board/" + boardId;
        final String title = "title";
        final String content = "content";
        final BoardDetailPostReqDto boardDetailPostReqDto = new BoardDetailPostReqDto(title, content);
        final String requestBody = om.writeValueAsString(boardDetailPostReqDto);

        // when
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USERID", "alexgim")
                .content(requestBody));
        String responseBy = result.andReturn().getResponse().getContentAsString();
        System.out.println(responseBy);


        // then
        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 단건 조회 API")
    public void readOneBoardDetailTest() throws Exception {
        // given
        final Long boardId = 1L;
        final Long boardDetailId = 1L;
        final String url = "/api/board/" + boardId + "/" + boardDetailId;

        Board board1 = newMockBoard(1L, "title1", null);
        for(int i = 1; i <= 20; i++) {
            BoardDetail boardDetail = newMockBoardDetail((long) i, "name", "username", "content", null, LocalDateTime.now(), LocalDateTime.now());
            boardDetail.setBoard(board1);
        }
        Long id = boardDetailId;
        given(boardDetailService.readOneBoardDetail(boardId, boardDetailId)).willReturn(BoardDetailRespDto.fromEntity(board1.getBoardDetails().get(id.intValue())));

        // when
        ResultActions result = mockMvc.perform(get(url));
        String responseBody = result.andReturn().getResponse().getContentAsString();
        System.out.println(responseBody);

        // then
        result.andExpect(status().isOk());

    }

    @Test
    @DisplayName("게시글 수정 API")
    public void updateBoardDetailTest() throws Exception {
        final Long boardId = 1L;
        final Long boardDetailId = 1L;
        final String userId = "alexgim";
        final BoardDetailPutReqDto putReqDto = BoardDetailPutReqDto.builder()
                                                                .name("chang")
                                                                .content("contentChange")
                                                                .build();

        final String url = "/api/board/" + boardId + "/" + boardDetailId;

        given(boardDetailService.updateBoardDetail(boardId, boardDetailId, userId, putReqDto)).willReturn(new BoardDetailRespDto(1L, putReqDto.getName(), putReqDto.getContent(), userId, 0L, LocalDateTime.now().toString(), LocalDateTime.now().toString()));
        String requestBody = om.writeValueAsString(putReqDto);


        ResultActions result = mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("X-USERID", userId)
                .content(requestBody));

        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시글 삭제 API")
    public void deleteBoardDetailTest() throws Exception {
        final Long boardId = 1L;
        final Long boardDetailId = 1L;
        final String userId = "alexgim";
        final String url = "/api/board/" + boardId + "/" + boardDetailId;

        given(boardDetailService.deleteBoardDetail(any(), any(), any())).willReturn(boardDetailId);

        ResultActions result = mockMvc.perform(delete(url)
                .header("X-USERID", userId));


        result.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("게시판에 속한 게시글 목록 조회 API")
    public void readAllBoardTest() throws Exception {
        final Long boardId = 1L;
        final Integer page = 1;
        final Integer limit = 10;
        final String keyword = "key";
        final String url = "/api/board/" + boardId;
        CustomPage<BoardDetailRespDto> customPage = new CustomPage<>();

        given(boardDetailService.readBySearchPaging(any(), any(), any())).willReturn(customPage);
        given(boardDetailService.readPaging(any(), any())).willReturn(customPage);

        ResultActions result = mockMvc.perform(get(url)
                .param("page", String.valueOf(page))
                .param("limit", String.valueOf(limit)));

        result.andExpect(status().isOk());
    }

    @Test
    @DisplayName("게시판에 속한 게시글 목록 조회 API (키워드 + 페이징)")
    public void readAllBoardWithKeywordTest() throws Exception {
        final Long boardId = 1L;
        final Integer page = 1;
        final Integer limit = 10;
        final String keyword = "key";
        final String url = "/api/board/" + boardId;
        CustomPage<BoardDetailRespDto> customPage = new CustomPage<>();

        given(boardDetailService.readBySearchPaging(any(), any(), any())).willReturn(customPage);
        given(boardDetailService.readPaging(any(), any())).willReturn(customPage);

        ResultActions result = mockMvc.perform(get(url)
                .param("page", String.valueOf(page))
                .param("limit", String.valueOf(limit))
                .param("keyword", keyword));

        result.andExpect(status().isOk());
    }

}