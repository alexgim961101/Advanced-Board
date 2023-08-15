package com.mailplug.homework.service;

import com.mailplug.homework.domain.board.Board;
import com.mailplug.homework.domain.board.BoardRepository;
import com.mailplug.homework.web.dto.BoardPostReqDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;

    // TODO: return 값 생각해보기
    @Transactional
    public void saveBoard(BoardPostReqDto boardPostReqDto){
        Board board = Board.createBoard(boardPostReqDto.getTitle());
        boardRepository.save(board);
    }
}
