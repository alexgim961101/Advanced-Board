package com.mailplug.homework.web.api;

import com.mailplug.homework.service.BoardDetailService;
import com.mailplug.homework.util.SystemString;
import com.mailplug.homework.util.ValidCheck;
import com.mailplug.homework.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/board")
public class BoardController {
    private final BoardDetailService boardDetailService;

    // TODO: 게시판 생성은 ADMIN만 할 수 있도록 변경
//    @PostMapping()
//    public ResponseEntity<?> createBoard(@RequestHeader("X-USERID") String userId, BoardPostReqDto boardPostReqDto, BindingResult bindingResult) {
//        log.info("--- [createBoard] X-USERID : {}", userId);
//        ValidCheck.validCheck(bindingResult);
//        boardService.saveBoard(boardPostReqDto);
//        return null;
//    }

    /**
     * 게시글 등록 API
     * */
    @PostMapping("/{boardId}")
    public ResponseEntity<CMRespDto<Long>> createBoardDetail(@PathVariable("boardId") Long boardId, @RequestHeader("X-USERID") String userId, @Valid @RequestBody BoardDetailPostReqDto boardDetailPostReqDto, BindingResult bindingResult) {
        log.info("--- [createPost] X-USERID : {} ---", userId);
        ValidCheck.validCheck(bindingResult);
        Long id = boardDetailService.saveBoardDetail(boardId, userId, boardDetailPostReqDto);

        return new ResponseEntity<>(new CMRespDto<>(HttpStatus.CREATED.value(), SystemString.SUCCESS_CREATE_BOARD_DETAIL.getMessage(), id), HttpStatus.CREATED);
    }

    /**
     * 게시글 단건 조회 API
     * */
    @GetMapping("/{boardId}/{boardDetailId}")
    public ResponseEntity<CMRespDto<BoardDetailRespDto>> readOneBoardDetail(@PathVariable("boardId") Long boardId, @PathVariable("boardDetailId") Long boardDetailId) {
        log.info("--- [readOneBoardDetail] boardId : {} , boardDetailId : {} ---", boardId, boardDetailId);
        ValidCheck.validCheck(boardId, boardDetailId);

        BoardDetailRespDto dto = boardDetailService.readOneBoardDetail(boardId, boardDetailId);
        return new ResponseEntity<>(new CMRespDto<>(HttpStatus.OK.value(), SystemString.SUCCESS_READ_ONE_BOARD_DETAIL.getMessage(), dto), HttpStatus.OK);
    }

    /**
     * 게시글 수정 API
     * */
    @PutMapping("/{boardId}/{boardDetailId}")
    public ResponseEntity<CMRespDto<BoardDetailRespDto>> updateBoardDetail(@PathVariable("boardId") Long boardId, @PathVariable("boardDetailId") Long boardDetailId, @RequestHeader("X-USERID") String userId, @Valid @RequestBody BoardDetailPutReqDto boardDetailPutReqDto, BindingResult bindingResult) {
        log.info("--- [updateBoardDetail] boardId : {} , boardDetailId : {} , userId : {} ---", boardId, boardDetailId, userId);
        ValidCheck.validCheck(boardId, boardDetailId);
        ValidCheck.validCheck(bindingResult);

        BoardDetailRespDto boardDetailRespDto = boardDetailService.updateBoardDetail(boardId, boardDetailId, userId, boardDetailPutReqDto);
        return new ResponseEntity<>(new CMRespDto<>(HttpStatus.CREATED.value(), SystemString.SUCCESS_UPDATE_BOARD_DETAIL.getMessage(), boardDetailRespDto), HttpStatus.CREATED);
    }


    /**
     * 게시글 삭제 API
     * */
    @DeleteMapping("/{boardId}/{boardDetailId}")
    public ResponseEntity<CMRespDto<Long>> deleteBoardDetail(@PathVariable("boardId") Long boardId, @PathVariable("boardDetailId") Long boardDetailId, @RequestHeader("X-USERID") String userId) {
        log.info("--- [deleteBoardDetail] boardId : {} , boardDetailId : {} , userId : {} ---", boardId, boardDetailId, userId);
        ValidCheck.validCheck(boardId, boardDetailId);

        Long id = boardDetailService.deleteBoardDetail(boardId, boardDetailId, userId);
        return new ResponseEntity<>(new CMRespDto<>(HttpStatus.CREATED.value(), SystemString.SUCCESS_DELETE_BOARD_DETAIL.getMessage(), id), HttpStatus.CREATED);
    }


    /**
     * 게시판에 속한 게시글 목록 조회 API
     * */
    @GetMapping("/{boardId}")
    public ResponseEntity<CMRespDto<CustomPage<BoardDetailRespDto>>> readAllBoard(@PathVariable Long boardId, @RequestParam Integer page, @RequestParam Integer limit, @RequestParam(required = false) String search) {
        log.info("--- [deleteBoardDetail] boardId : {} , page : {} , limit : {} ---", boardId, page, limit);
        page--;
        ValidCheck.validCheck(boardId, page, limit);

        PageRequest pageObj = PageRequest.of(page, limit);
        CustomPage<BoardDetailRespDto> customPage;
        if(search != null) {
            customPage = boardDetailService.readBySearchPaging(boardId, search, pageObj);
        } else {
            customPage = boardDetailService.readPaging(boardId, pageObj);
        }

        return new ResponseEntity<>(new CMRespDto<>(HttpStatus.OK.value(), SystemString.SUCCESS_READ_ALL_BOARD_DETAIL.getMessage(), customPage), HttpStatus.OK);
    }
}
