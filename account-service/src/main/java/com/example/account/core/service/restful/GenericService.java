package com.example.account.core.service.restful;

import java.lang.reflect.Type;

import com.example.account.core.dao.QueryExecutor;
import com.example.account.core.dao.QuerydslExecutor;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ResolvableType;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.Getter;

@Getter
public abstract class GenericService<T, E, ID> {
    @Autowired
    private JpaRepository<E, ID> repository;

    @Autowired
    @Qualifier("notNullModelMapper")
    private ModelMapper notNullModelMapper;

    private QueryExecutor<E, ?> executor;

    private ModelMapper modelMapper;

    private Class<T> dtoClass;

    private Class<E> entityClass;

    @Autowired
    public void setExecutor(JPAQueryFactory factory) {
        this.executor = new QuerydslExecutor<E>() {
            @Override
            public JPAQueryFactory getFactory() {
                return factory;
            }

            @Override
            public Class<E> getEntityClass() {
                return GenericService.this.getEntityClass();
            }
        };
    }

    @Autowired
    @Qualifier("modelMapper")
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        this.dtoClass = getGenericClass(0);
        this.entityClass = getGenericClass(1);
    }

    public T toDto(E t) {
        return getModelMapper().map(t, (Type) getDtoClass());
    }

    public E toEntity(T b) {
        return getModelMapper().map(b, (Type) getEntityClass());
    }

    public <S> Class<S> getGenericClass(int index) {
        return getModelMapper().map(
                ResolvableType.forClass(GenericService.class, getClass()).getGeneric(index).resolve(),
                (Type) Class.class);
    }
}
