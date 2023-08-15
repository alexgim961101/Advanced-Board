package com.mailplug.homework.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomPage <T>{
    private List<T> contents = new ArrayList<>();
    private Long totalPages;
    private Long totalElements;
    private boolean last;
    private Long size;
    private Long number;
    private Long numberOfElements;
    private boolean first;
    private boolean empty;
}
