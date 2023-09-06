package com.example.account.core.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.example.account.core.service.restful.JpaService;
import com.example.account.core.service.restful.RestfulService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import lombok.Getter;

public abstract class JpaController<T, E, Q, ID> extends RestfulController<T, Q, ID>{
    @Autowired
    private JpaRepository<E, ID> repository;

    @Autowired
    private JpaSpecificationExecutor<E> executor;
    
    private final Type[] typeList = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

    @Getter
    private final RestfulService<T, ID> service = new JpaService<T, E, ID>(){
        @Override
        public JpaRepository<E, ID> getRepository(){
            return JpaController.this.repository;
        }

        @Override
        public JpaSpecificationExecutor<E> getExecutor(){
            return JpaController.this.executor;
        }

        @Override
        public Type[] getTypeList(){
            return JpaController.this.typeList;
        }
    };
}