package com.mailplug.homework.domain.boardDetail;

import com.mailplug.homework.domain.BaseEntity;
import com.mailplug.homework.domain.board.Board;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "boardDetail", indexes = {@Index(name = "fk_pk_index", columnList = "board_id,id")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BoardDetail extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    @Setter
    private String name;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    @Setter
    private String content;
    // TODO: 생성 시 조회수 0으로 초기화
    // TODO: 동시성 고려하기
    private Long count;
    @Enumerated(EnumType.STRING)
    @Setter
    private BoardDetailStatus status;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "board_id")
    private Board board;

    @Builder
    protected BoardDetail(Long id, String name, String username, String content, Long count, BoardDetailStatus status, Board board, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.content = content;
        this.count = count;
        this.status = status;
        this.board = board;
        super.createdAt = createdAt;
        super.updatedAt = updatedAt;
    }

    //== 생성 메서드 ==//
    public static BoardDetail createBoardDetail(String name, String username, String content) {
        return BoardDetail.builder()
                .name(name)
                .content(content)
                .username(username)
                .status(BoardDetailStatus.EXIST)
                .count(0L)
                .build();
    }

    //== 연관관계 메서드 ==//
    public void setBoard(Board board) {
        board.getBoardDetails().add(this);
        this.board = board;
    }

    //== 기타 행동 메서드 ==//
    public void addCount(){
        this.count += 1;
    }
}
