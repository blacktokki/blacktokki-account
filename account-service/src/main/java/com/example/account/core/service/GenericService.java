package com.example.account.core.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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

public abstract class GenericService<T, B, ID> {
    @Autowired
    protected JpaRepository<T, ID> repository;

    @Autowired
    protected JpaSpecificationExecutor<T> specificationExecutor;

    protected final ModelMapper modelMapper = new ModelMapper();

    protected final ModelMapper notNullModelMapper = new ModelMapper();

    private final CustomJsr310Module customJsr310Module = new CustomJsr310Module();

    protected Type[] typeList = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
    
    @PostConstruct
    public void postConstruct(){
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.registerModule(customJsr310Module);
        notNullModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        notNullModelMapper.registerModule(customJsr310Module);
    }

    public PageResponseDto<B> getPage(Pageable pageable, Specification<T> spec){
        Page<T> result = specificationExecutor.findAll(spec, pageable);
        Page<B> mappedResult = result.map((data)->toDto(data));
        return new PageResponseDto<B>(mappedResult);
    }

    public List<B> getList(Specification<T> spec, Sort sort){
        List<T> result = specificationExecutor.findAll(spec, sort);
        return result.stream().map((data)->toDto(data)).collect(Collectors.toList());
    }

    public B get(ID id){
        return toDto(repository.findById(id).get());
    }

    @Transactional
    public B update(ID id, B updated){
        B dto = get(id);
        try {
            BeanUtils.copyProperties(updated, dto);
        }
        catch (Exception e) {
        }
        T saved = this.repository.save(toEntity(dto));
        return toDto(saved);
    }

    @Transactional
    public B bulkUpdateFields(List<ID> ids, B updated) {
        List<T> entityList = repository.findAllById(ids);
        for (T entity: entityList){
            notNullModelMapper.map(updated, entity);
        }
        repository.saveAll(entityList);
        return updated;
    }

    @Transactional
    public B create(B newDomain){
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

    protected B toDto(T t){
        return modelMapper.map(t, typeList[1]);
    }
    protected T toEntity(B b){
        return modelMapper.map(b, typeList[0]);
    }
}