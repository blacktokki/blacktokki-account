package com.example.account.core.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import com.example.account.core.config.CustomJsr310Module;
import com.example.account.core.dto.PageResponseDto;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public abstract class JpaService<T, E, ID> implements RestService<T, Specification<E>, ID>{
    @Autowired
    protected JpaRepository<E, ID> repository;

    @Autowired
    protected JpaSpecificationExecutor<E> specificationExecutor;

    protected Type[] typeList = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

    protected final ModelMapper modelMapper = new ModelMapper();

    protected final ModelMapper notNullModelMapper = new ModelMapper();
    
    {
        CustomJsr310Module customJsr310Module = new CustomJsr310Module();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.registerModule(customJsr310Module);
        notNullModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        notNullModelMapper.registerModule(customJsr310Module);
    }

    @Override
    public PageResponseDto<T> getPage(Pageable pageable, Specification<E> spec){
        Page<E> result = specificationExecutor.findAll(spec, pageable);
        Page<T> mappedResult = result.map((data)->toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    @Override
    public List<T> getList(Specification<E> spec, Sort sort){
        List<E> result = specificationExecutor.findAll(spec, sort);
        return result.stream().map((data)->toDto(data)).collect(Collectors.toList());
    }

    public T get(ID id){
        return toDto(repository.findById(id).get());
    }

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

    @Transactional
    public T bulkUpdateFields(List<ID> ids, T updated) {
        List<E> entityList = repository.findAllById(ids);
        for (E entity: entityList){
            notNullModelMapper.map(updated, entity);
        }
        repository.saveAll(entityList);
        return updated;
    }

    @Transactional
    public T create(T newDomain){
        return toDto(repository.save(toEntity(newDomain)));
    }

    @Transactional
    public void delete(ID id){
        //check if object with this id exists
        get(id);
        repository.deleteById(id);
    }

    @Transactional
    public void bulkDelete(List<ID> ids) {
        repository.deleteAllById(ids);  
    }

    protected T toDto(E t){
        return modelMapper.map(t, typeList[0]);
    }
    protected E toEntity(T b){
        return modelMapper.map(b, typeList[1]);
    }
}