package com.example.account.core.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;

public abstract class JpaService<T, E, ID> extends MapperService<T, E> implements RestfulService<T, Specification<E>, ID>, SpecificationService<T, E>{
    @Autowired
    protected JpaRepository<E, ID> repository;

    @Autowired
    @Getter
    protected JpaSpecificationExecutor<E> executor;

    @Override
    public T get(ID id){
        return toDto(repository.findById(id).get());
    }

    @Override
    @Transactional
    public T update(ID id, T updated){
        T dto = get(id);
        try {
            BeanUtils.copyProperties(updated, dto);
        }
        catch (Exception e) {
        }
        E saved = this.repository.save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    @Transactional
    public T bulkUpdateFields(List<ID> ids, T updated) {
        List<E> entityList = repository.findAllById(ids);
        for (E entity: entityList){
            notNullModelMapper.map(updated, entity);
        }
        repository.saveAll(entityList);
        return updated;
    }

    @Override
    @Transactional
    public T create(T newDomain){
        return toDto(repository.save(toEntity(newDomain)));
    }

    @Override
    @Transactional
    public void delete(ID id){
        //check if object with this id exists
        get(id);
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void bulkDelete(List<ID> ids) {
        repository.deleteAllById(ids);  
    }
}