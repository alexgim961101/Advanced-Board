package com.mailplug.homework.web.api;

import com.mailplug.homework.service.BoardDetailService;
import com.mailplug.homework.service.BoardService;
import com.mailplug.homework.util.ValidCheck;
import com.mailplug.homework.web.dto.BoardDetailPostReqDto;
import com.mailplug.homework.web.dto.BoardPostReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/board")
public class BoardController {
    private final BoardDetailService boardDetailService;
    private final BoardService boardService;

    // TODO: 미완성
    // TODO: 게시판 생성은 ADMIN만 할 수 있도록 변경
    @PostMapping()
    public ResponseEntity<?> createBoard(@RequestHeader("X-USERID") String userId, BoardPostReqDto boardPostReqDto, BindingResult bindingResult) {
        log.info("--- [createBoard] X-USERID : {}", userId);
        ValidCheck.validCheck(bindingResult);
        boardService.saveBoard(boardPostReqDto);
        return null;
    }

    @PostMapping("/{boardId}")
    public ResponseEntity<?> createPost(@PathVariable("boardId") Long boardId, @RequestHeader("X-USERID") String userId, @RequestBody BoardDetailPostReqDto boardDetailPostReqDto, BindingResult bindingResult) {
        log.info("--- [createPost] X-USERID : {} ---", userId);
        ValidCheck.validCheck(bindingResult);
        boardDetailService.savePost(boardId, userId, boardDetailPostReqDto);

        return null;
    }
}
