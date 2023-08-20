package com.mailplug.homework.service;

import com.mailplug.homework.domain.board.Board;
import com.mailplug.homework.domain.board.BoardRepository;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import com.mailplug.homework.domain.boardDetail.BoardDetailRepository;
import com.mailplug.homework.domain.boardDetail.BoardDetailStatus;
import com.mailplug.homework.handler.ex.CustomApiException;
import com.mailplug.homework.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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

    /**
     * 게시물 삭제
     * */
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

    /**
     * 게시물 조회 (페이징)
     * */

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "boardDetail", key = "#boardDetailId + ':' + #pageRequest.pageNumber + ':' + #pageRequest.offset", cacheManager = "rcm")
    public CustomPage<BoardDetailRespDto> readPaging(Long boardId, PageRequest pageRequest) {
        Page<BoardDetail> boardDetailPageEntity = boardDetailRepository.findAllByBoardIdByStatusOrderByIdDesc(boardId, BoardDetailStatus.EXIST, pageRequest);

        CustomPage<BoardDetailRespDto> page = new CustomPage<>();

        for(BoardDetail boardDetail : boardDetailPageEntity.getContent()) {
            BoardDetailRespDto boardDetailGetRespPagingDto = BoardDetailRespDto.fromEntity(boardDetail);
            page.getContents().add(boardDetailGetRespPagingDto);
        }

        page.setPage(boardDetailPageEntity);

        return page;
    }

    /**
     * 게시물 조회 (검색 + 페이징)
     * */
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

    /**
     * 게시물 단건 조회
     * */
    @Transactional()
    @CacheEvict(cacheNames = "boardDetail", allEntries = true)
    public BoardDetailRespDto readOneBoardDetail(Long boardId, Long boardDetailId) {
        BoardDetail boardDetailEntity = boardDetailRepository.findBoardDetail(boardId, boardDetailId).orElseThrow(() -> {
            throw new CustomApiException(NOT_FOUND_BOARD_DETAIL.isSuccess(), NOT_FOUND_BOARD_DETAIL.getMessage());
        });

        if(boardDetailEntity.getStatus() == BoardDetailStatus.DELETE) throw new CustomApiException(ALREADY_DELETED.isSuccess(), ALREADY_DELETED.getMessage());

        boardDetailEntity.addCount();
        return BoardDetailRespDto.fromEntity(boardDetailEntity);
    }

    /**
     * 게시물 수정
     * */
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
}
