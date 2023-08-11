package com.mailplug.homework.domain.board;

import com.mailplug.homework.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "board")
public class Board extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
}
