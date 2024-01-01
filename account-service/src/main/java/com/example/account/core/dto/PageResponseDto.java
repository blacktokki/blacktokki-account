package com.example.account.core.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class PageResponseDto<T> {

    private List<T> value;
    private int count;
    private int offset;
    private int limit;
    private long total;

    @Builder
    public PageResponseDto(List<T> value, int count, int offset, int limit, long total) {
        this.value = value;
        this.count = count;
        this.offset = offset;
        this.limit = limit;
        this.total = total;
    }

    public PageResponseDto(Page<T> page) {
        this.value = page.getContent();
        this.count = page.getNumberOfElements();
        this.offset = page.getPageable().getPageNumber();
        this.limit = page.getPageable().getPageSize();
        this.total = page.getTotalElements();
    }
}