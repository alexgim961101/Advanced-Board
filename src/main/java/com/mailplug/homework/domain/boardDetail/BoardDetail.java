package com.mailplug.homework.domain.boardDetail;

import com.mailplug.homework.domain.BaseEntity;
import com.mailplug.homework.domain.board.Board;
import jakarta.persistence.*;

@Entity
@Table(name = "boardDetail")
public class BoardDetail extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String content;
    private Long count;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
}
