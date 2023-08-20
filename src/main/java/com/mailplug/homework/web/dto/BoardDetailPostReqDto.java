package com.mailplug.homework.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
public class BoardDetailPostReqDto {
    @Size(max = 100)
    @NotBlank
    private String name;
    @NotBlank
    private String content;

    @Builder
    public BoardDetailPostReqDto(String name, String content) {
        this.name = name;
        this.content = content;
    }
}
