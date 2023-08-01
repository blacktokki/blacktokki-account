package com.example.account.core.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.example.account.core.dto.PageResponseDto;
import com.querydsl.core.types.Predicate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QuerydslService<T, E> {
    public QuerydslPredicateExecutor<E> getExecutor();

    default PageResponseDto<T> getPage(Predicate pred, Pageable pageable){
        Page<E> result = getExecutor().findAll(pred, pageable);
        Page<T> mappedResult = result.map((data)->toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    default List<T> getList(Predicate pred, Sort sort){
        Iterable<E> result = getExecutor().findAll(pred, sort);
        return StreamSupport.stream(result.spliterator(), false).map((data)->toDto(data)).collect(Collectors.toList());
    }

    public T toDto(E t);
}
