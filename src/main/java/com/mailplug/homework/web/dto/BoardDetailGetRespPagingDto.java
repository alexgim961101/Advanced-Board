package com.mailplug.homework.web.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.mailplug.homework.domain.boardDetail.BoardDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Builder
public class BoardDetailGetRespPagingDto {
    private Long id;
    private String title;
    private String username;
    private String createdAt;
    private Long count;

    public static BoardDetailGetRespPagingDto fromEntity(BoardDetail boardDetail) {
        return BoardDetailGetRespPagingDto.builder()
                .id(boardDetail.getId())
                .title(boardDetail.getName())
                .username(boardDetail.getUsername())
                .createdAt(DateTimeFormatter.ofPattern("YY-MM-dd").format(boardDetail.getUpdatedAt()).toString())
                .count(boardDetail.getCount())
                .build();
    }
}
