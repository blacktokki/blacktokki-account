package com.example.account.core.service;

import java.util.List;

import com.example.account.core.dto.PageResponseDto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface RestfulService<T, P, ID> {
    public PageResponseDto<T> getPage(P param, Pageable pageable);

    public List<T> getList(P param, Sort sort);

    public T get(ID id);

    public T update(ID id, T updated);

    public T bulkUpdateFields(List<ID> ids, T updated);

    public T create(T newDomain);

    public void delete(ID id);

    public void bulkDelete(List<ID> ids);
}