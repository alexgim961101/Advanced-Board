package com.mailplug.homework.web.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BoardPostReqDto {
    @NotEmpty
    private String title;
}
