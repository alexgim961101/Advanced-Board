package com.mailplug.homework.web.dto;

import com.mailplug.homework.domain.boardDetail.BoardDetail;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardDetailPutReqDto {
    @Size(max = 100)
    @NotBlank
    private String name;
    @NotBlank
    private String content;

    @Builder
    public BoardDetailPutReqDto(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public void setBoardEntity(BoardDetail boardDetail) {
        boardDetail.setName(this.name);
        boardDetail.setContent(this.content);
    }
}
