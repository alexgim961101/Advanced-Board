package com.mailplug.homework.domain.board;

import com.mailplug.homework.domain.BaseEntity;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
public class Board extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<BoardDetail> boardDetails = new ArrayList<>();
}
