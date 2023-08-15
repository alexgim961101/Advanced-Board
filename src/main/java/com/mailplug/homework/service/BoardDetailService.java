package com.mailplug.homework.service;

import com.mailplug.homework.domain.board.Board;
import com.mailplug.homework.domain.board.BoardRepository;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import com.mailplug.homework.domain.boardDetail.BoardDetailRepository;
import com.mailplug.homework.domain.boardDetail.BoardDetailStatus;
import com.mailplug.homework.handler.ex.CustomApiException;
import com.mailplug.homework.web.dto.BoardDetailGetRespPagingDto;
import com.mailplug.homework.web.dto.BoardDetailPostReqDto;
import com.mailplug.homework.web.dto.BoardDetailPutReqDto;
import com.mailplug.homework.web.dto.CustomPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mailplug.homework.util.SystemString.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardDetailService {
    private final BoardDetailRepository boardDetailRepository;
    private final BoardRepository boardRepository;

    /**
     * 게시물 생성
     * */
    @Transactional
    public void savePost(Long boardId, String userId, BoardDetailPostReqDto boardDetailPostReqDto) {
        BoardDetail boardDetail = BoardDetail.createBoardDetail(boardDetailPostReqDto.getName(),
                                                                userId,
                                                                boardDetailPostReqDto.getContent());

        Board boardEntity = boardRepository.findById(boardId).orElseThrow(() -> {
            throw new CustomApiException(NOT_FOUND_BOARD.isSuccess(), NOT_FOUND_BOARD.getMessage());
        });

        boardDetail.setBoard(boardEntity);
        boardDetailRepository.save(boardDetail);
    }

    /**
     * 게시물 수정
     * */
    @Transactional
    public void updatePost(Long boardId, Long boardDetailId, String userId, BoardDetailPutReqDto boardDetailPutReqDto) {
        BoardDetail boardDetailEntity = boardDetailRepository.findById(boardDetailId).orElseThrow(() -> {
            throw new CustomApiException(NOT_FOUND_BOARD_DETAIL.isSuccess(), NOT_FOUND_BOARD_DETAIL.getMessage());
        });

        if(!boardDetailEntity.getUsername().equals(userId)) throw new CustomApiException(DO_NOT_MATCH_USERNAME.isSuccess(), DO_NOT_MATCH_USERNAME.getMessage());

        boardDetailEntity.setName(boardDetailEntity.getName());
        boardDetailEntity.setContent(boardDetailEntity.getContent());
    }

    /**
     * 게시물 삭제
     * */
    @Transactional
    public void deletePost(Long boardId, Long boardDetailId, String userId) {
        BoardDetail boardDetailEntity = boardDetailRepository.findById(boardDetailId).orElseThrow(() -> {
            throw new CustomApiException(NOT_FOUND_BOARD_DETAIL.isSuccess(), NOT_FOUND_BOARD_DETAIL.getMessage());
        });

        if(!boardDetailEntity.getUsername().equals(userId)) throw new CustomApiException(DO_NOT_MATCH_USERNAME.isSuccess(), DO_NOT_MATCH_USERNAME.getMessage());

        if(boardDetailEntity.getStatus().equals(BoardDetailStatus.DELETE)) throw new CustomApiException(ALREADY_DELETED.isSuccess(), ALREADY_DELETED.getMessage());
        boardDetailEntity.setStatus(BoardDetailStatus.DELETE);
    }

    /**
     * 게시물 조회 (페이징)
     * TODO: EXSIT 상태인 게시물만 출력하기
     * */
    @Transactional(readOnly = true)
    public CustomPage<BoardDetailGetRespPagingDto> readPaging(PageRequest pageRequest) {
        Page<BoardDetail> boardDetailPageEntity = boardDetailRepository.findAllByOrderByIdDesc(pageRequest);

        CustomPage<BoardDetailGetRespPagingDto> page = new CustomPage<>();

        for(BoardDetail boardDetail : boardDetailPageEntity.getContent()) {
            BoardDetailGetRespPagingDto boardDetailGetRespPagingDto = BoardDetailGetRespPagingDto.fromEntity(boardDetail);
            page.getContents().add(boardDetailGetRespPagingDto);
        }

        page.setTotalPages((long) boardDetailPageEntity.getTotalPages());
        page.setTotalElements(boardDetailPageEntity.getTotalElements());
        page.setLast(boardDetailPageEntity.isLast());
        page.setSize((long) boardDetailPageEntity.getSize());
        page.setNumber((long) boardDetailPageEntity.getNumber());
        page.setNumberOfElements((long) boardDetailPageEntity.getNumberOfElements());
        page.setFirst(boardDetailPageEntity.isFirst());
        page.setEmpty(page.isEmpty());

        return page;
    }

    /**
     * 게시물 조회 (검색 + 페이징)
     * TODO: EXSIT 상태인 게시물만 출력하기
     * */
    @Transactional(readOnly = true)
    public CustomPage<BoardDetailGetRespPagingDto> readBySearchPaging(String keyword, PageRequest pageRequest) {
        Page<BoardDetail> boardDetailPageEntity = boardDetailRepository.findAllByNameContainingOrderById(keyword, pageRequest);

        CustomPage<BoardDetailGetRespPagingDto> page = new CustomPage<>();

        for(BoardDetail boardDetail : boardDetailPageEntity.getContent()) {
            BoardDetailGetRespPagingDto boardDetailGetRespPagingDto = BoardDetailGetRespPagingDto.fromEntity(boardDetail);
            page.getContents().add(boardDetailGetRespPagingDto);
        }

        page.setTotalPages((long) boardDetailPageEntity.getTotalPages());
        page.setTotalElements(boardDetailPageEntity.getTotalElements());
        page.setLast(boardDetailPageEntity.isLast());
        page.setSize((long) boardDetailPageEntity.getSize());
        page.setNumber((long) boardDetailPageEntity.getNumber());
        page.setNumberOfElements((long) boardDetailPageEntity.getNumberOfElements());
        page.setFirst(boardDetailPageEntity.isFirst());
        page.setEmpty(page.isEmpty());

        return page;
    }
}
