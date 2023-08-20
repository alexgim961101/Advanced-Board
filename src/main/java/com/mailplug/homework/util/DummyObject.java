package com.mailplug.homework.util;

import com.mailplug.homework.domain.board.Board;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import com.mailplug.homework.domain.boardDetail.BoardDetailStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class DummyObject {
    protected Board newMockBoard(Long id, String title, List<BoardDetail> boardDetails) {
        return Board.builder()
                .id(id)
                .title(title)
                .boardDetails(boardDetails)
                .build();
    }

    protected BoardDetail newMockBoardDetail(Long id, String name, String username, String content, Board board, LocalDateTime createAt, LocalDateTime updatedAt) {
        return BoardDetail.builder()
                .id(id)
                .name(name)
                .content(content)
                .username(username)
                .status(BoardDetailStatus.EXIST)
                .count(0L)
                .board(board)
                .createdAt(createAt)
                .updatedAt(updatedAt)
                .build();
    }

    protected Page<BoardDetail> newMockPageBoardDetail(List<BoardDetail> boardDetails, Pageable pageable, Long total) {
        return new PageImpl<>(boardDetails, pageable, total);
    }
}
