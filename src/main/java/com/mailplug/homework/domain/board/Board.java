package com.mailplug.homework.domain.board;

import com.mailplug.homework.domain.BaseEntity;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import jakarta.persistence.*;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardDetail> boardDetails = new ArrayList<>();

    @Builder
    protected Board(Long id, String title, List<BoardDetail> boardDetails){
        this.id = id;
        this.title = title;
        if(boardDetails == null) this.boardDetails = new ArrayList<>();
        else this.boardDetails = boardDetails;
    }

    //== 생성 메서드 ==/
    public static Board createBoard(String title) {
        return Board.builder()
                .title(title)
                .build();
    }
}
