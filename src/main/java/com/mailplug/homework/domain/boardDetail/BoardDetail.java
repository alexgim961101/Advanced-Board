package com.mailplug.homework.domain.boardDetail;

import com.mailplug.homework.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "boardDetail")
public class BoardDetail extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String username;
    private String content;
}
