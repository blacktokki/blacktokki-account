package com.example.account.core.service.restful;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.criteria.Predicate;

import com.example.account.core.dao.QueryExecutor;
import com.example.account.core.dto.PageResponseDto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.Getter;

@Getter
public abstract class JpaQueryService<T, E, ID> extends MapperService<T, E> implements QueryService<T, ID>{
    @Autowired
    @Qualifier("modelMapper")
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("notNullModelMapper")
    private ModelMapper notNullModelMapper;

    @Autowired
    private JpaRepository<E, ID> repository;

    @Autowired
    private QueryExecutor<E, Predicate> executor;

    @Override
    public T get(ID id) {
        return toDto(getRepository().findById(id).get());
    }

    @Override
    public PageResponseDto<T> getPage(Object param, Pageable pageable) {
        Page<?> result = getExecutor().findAll(param, pageable);
        Page<T> mappedResult = result.map((data) -> toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    @Override
    public List<T> getList(Object param, Sort sort) {
        Iterable<?> result = getExecutor().findAll(param, sort);
        return StreamSupport.stream(result.spliterator(), false).map((data) -> toDto(data))
                .collect(Collectors.toList());
    }

}
