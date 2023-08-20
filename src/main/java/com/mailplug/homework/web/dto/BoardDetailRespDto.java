package com.mailplug.homework.web.dto;

import com.mailplug.homework.domain.boardDetail.BoardDetail;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class BoardDetailRespDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private Long count;
    private String createAt;
    private String updatedAt;

    @Builder
    public BoardDetailRespDto(Long id, String title, String content, String username, Long count, String createAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.count = count;
        this.createAt = createAt;
        this.updatedAt = updatedAt;
    }

    public static BoardDetailRespDto fromEntity(BoardDetail boardDetail) {
        return BoardDetailRespDto.builder()
                .id(boardDetail.getId())
                .title(boardDetail.getName())
                .content(boardDetail.getContent())
                .username(boardDetail.getUsername())
                .count(boardDetail.getCount())
                .createAt(DateTimeFormatter.ofPattern("YY-MM-dd").format(boardDetail.getCreatedAt()).toString())
                .updatedAt(DateTimeFormatter.ofPattern("YY-MM-dd").format(boardDetail.getUpdatedAt()).toString())
                .build();
    }
}
