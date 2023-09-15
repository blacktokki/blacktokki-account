package com.example.account.core.service.restful;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.example.account.core.dao.QueryExecutor;
import com.example.account.core.dto.PageResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaQueryService<T, E, ID> extends QueryService<T, ID> {
    public JpaRepository<E, ID> getRepository();

    public QueryExecutor<E, ?> getExecutor();

    public T toDto(E e);

    @Override
    default T get(ID id) {
        return toDto(getRepository().findById(id).get());
    }

    @Override
    default PageResponseDto<T> getPage(Object param, Pageable pageable) {
        Page<E> result = getExecutor().findAll(param, pageable);
        Page<T> mappedResult = result.map((data) -> toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    @Override
    default List<T> getList(Object param, Sort sort) {
        Iterable<E> result = getExecutor().findAll(param, sort);
        return StreamSupport.stream(result.spliterator(), false).map((data) -> toDto(data))
                .collect(Collectors.toList());
    }

}
