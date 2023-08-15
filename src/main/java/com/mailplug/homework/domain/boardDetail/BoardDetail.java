package com.mailplug.homework.domain.boardDetail;

import com.mailplug.homework.domain.BaseEntity;
import com.mailplug.homework.domain.board.Board;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boardDetail")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
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
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;

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

}
