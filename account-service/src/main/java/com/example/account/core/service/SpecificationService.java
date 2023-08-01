package com.example.account.core.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.account.core.dto.PageResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SpecificationService<T, E>{
    public JpaSpecificationExecutor<E> getExecutor();

    default PageResponseDto<T> getPage(Specification<E> spec, Pageable pageable){
        Page<E> result = getExecutor().findAll(spec, pageable);
        Page<T> mappedResult = result.map((data)->toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    default List<T> getList(Specification<E> spec, Sort sort){
        List<E> result = getExecutor().findAll(spec, sort);
        return result.stream().map((data)->toDto(data)).collect(Collectors.toList());
    }

    public T toDto(E t);
}
