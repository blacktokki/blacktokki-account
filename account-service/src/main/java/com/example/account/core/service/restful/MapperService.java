package com.example.account.core.service.restful;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.example.account.core.config.CustomJsr310Module;

import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import lombok.Getter;

@Getter
public abstract class MapperService<T, E>{
    private Type[] typeList = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

    private final ModelMapper modelMapper = new ModelMapper();

    private final ModelMapper notNullModelMapper = new ModelMapper();
    
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