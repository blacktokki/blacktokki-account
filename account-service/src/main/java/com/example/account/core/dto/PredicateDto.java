package com.example.account.core.dto;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;

public abstract class PredicateDto<T> implements Serializable{
    @SuppressWarnings("unchecked")
    private final Class<T> classT = (Class<T>)((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    
    private final Path<T> root = Expressions.path(classT, "root");

    public Predicate toPredicate() {
        Predicate[] predicates = toPredicates(root);
        if (predicates.length > 0){
            return Expressions.predicate(Ops.AND, predicates);
        }
        return null;
    }

    abstract protected Predicate[] toPredicates(Path<T> root);

    protected Predicate toPredicateField(String key, Object value, Path<?> path){
        if (value != null){
            return Expressions.predicate(Ops.EQ, Expressions.path(Object.class, path, key), Expressions.constant(value));
        }
        return null;
    }
}
