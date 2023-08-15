package com.mailplug.homework.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BoardDetailPutReqDto {
    @Size(max = 100)
    @NotBlank
    private String name;
    @NotBlank
    private String content;
}
