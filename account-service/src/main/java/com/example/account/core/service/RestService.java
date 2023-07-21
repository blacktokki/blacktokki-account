package com.example.account.core.service;

import java.util.List;

import com.example.account.core.dto.PageResponseDto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface RestService<T, S, ID> {
    public PageResponseDto<T> getPage(Pageable pageable, S spec);

    public List<T> getList(S spec, Sort sort);

    public T get(ID id);

    public T update(ID id, T updated);

    public T bulkUpdateFields(List<ID> ids, T updated);

    public T create(T newDomain);

    public void delete(ID id);

    public void bulkDelete(List<ID> ids);
}