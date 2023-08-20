package com.mailplug.homework.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
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

    @Builder
    public CustomPage(List<T> contents, Long totalPages, Long totalElements, boolean last, Long size, Long number, Long numberOfElements, boolean first, boolean empty) {
        if(contents == null) this.contents = new ArrayList<>();
        else this.contents = contents;

        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.last = last;
        this.size = size;
        this.number = number;
        this.numberOfElements = numberOfElements;
        this.first = first;
        this.empty = empty;
    }

    public void setPage(Page page) {
        this.totalPages = (long) page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.last = page.isLast();
        this.size = (long) page.getSize();
        this.number = (long) page.getNumber();
        this.numberOfElements = (long) page.getNumberOfElements();
        this.first = page.isFirst();
        this.empty = page.isEmpty();
    }
}
