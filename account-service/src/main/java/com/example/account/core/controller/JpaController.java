package com.example.account.core.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.persistence.criteria.Predicate;

import com.example.account.core.service.restful.JpaService;
import com.example.account.core.dao.QueryExecutor;
import com.example.account.core.service.restful.CommandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.Getter;

public abstract class JpaController<T, E, Q, ID> extends RestfulController<T, Q, ID>{
    @Autowired
    private JpaRepository<E, ID> repository;

    @Autowired
    private QueryExecutor<E, Predicate> executor;
    
    private final Type[] typeList = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

    @Getter
    private final CommandService<T, ID> service = new JpaService<T, E, ID>(){
        @Override
        public JpaRepository<E, ID> getRepository(){
            return JpaController.this.repository;
        }

        @Override
        public QueryExecutor<E, Predicate> getExecutor(){
            return JpaController.this.executor;
        }

        @Override
        public Type[] getTypeList(){
            return JpaController.this.typeList;
        }
    };
}