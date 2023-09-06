package com.example.account.core.service.restful;

import java.beans.IntrospectionException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.Entity;

import com.example.account.core.dto.PageResponseDto;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.util.StringUtils;

public interface QuerydslService<T, E> extends QueryService<T>, PredicateService<Predicate>{
    public QuerydslPredicateExecutor<E> getExecutor();
    
    public T toDto(E entity);

    public Type[] getGenericTypeList();

    @Override
    default PageResponseDto<T> getPage(Object param, Pageable pageable){
        Page<E> result = getExecutor().findAll(toPredicate(param), pageable);
        Page<T> mappedResult = result.map((data)->toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    @Override
    default List<T> getList(Object param, Sort sort){
        Iterable<E> result = getExecutor().findAll(toPredicate(param), sort);
        return StreamSupport.stream(result.spliterator(), false).map((data)->toDto(data)).collect(Collectors.toList());
    }

    default Path<?> getRoot(){
        Class<?> entityClass = (Class<?>)getGenericTypeList()[1];
        String defaultName = StringUtils.uncapitalize(entityClass.getSimpleName());
        String name = Optional.ofNullable(entityClass.getAnnotation(Entity.class).name()).filter(v->!v.isEmpty()).orElse(defaultName);
        return Expressions.path(entityClass, name);
    };

    default Predicate toPredicate(String key, Object value){
        if(value != null){
            return Expressions.predicate(Ops.EQ, Expressions.path(Object.class, getRoot(), key), Expressions.constant(value));
        }
        return null;
    }

    default Predicate toPredicate(Object param) {
        Predicate[] predicates;
        try {
            predicates = toPredicates(param, QuerydslService.this::toPredicate).toArray(Predicate[]::new);
        } catch (IntrospectionException e) {
            predicates = new Predicate[]{};
        }
        if (predicates.length > 1){
            return Expressions.predicate(Ops.AND, predicates);
        }
        if (predicates.length == 1){
            return predicates[0];
        }
        return Expressions.TRUE.isTrue();
    }
}
