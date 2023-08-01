package com.example.account.core.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.example.account.core.config.CustomJsr310Module;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

public abstract class MapperService<T, E>{
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

    public T toDto(E t){
        return modelMapper.map(t, typeList[0]);
    }
    public E toEntity(T b){
        return modelMapper.map(b, typeList[1]);
    }
}