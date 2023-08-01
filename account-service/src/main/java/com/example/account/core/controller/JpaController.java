package com.example.account.core.controller;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.PostConstruct;

import com.example.account.core.service.JpaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public abstract class JpaController<T, E, Q extends Specification<E>, ID> extends RestfulController<T, Q, ID>{
    @Autowired
    protected JpaRepository<E, ID> repository;

    @Autowired
    protected JpaSpecificationExecutor<E> executor;
    
    private Type[] typeList = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

    @PostConstruct
    public void postConstruct(){
        if (service==null){
            service = new JpaService<T, E, ID>(){
                {
                    this.repository = JpaController.this.repository;
                    this.executor = JpaController.this.executor;
                    this.typeList = JpaController.this.typeList;
                }
            };
        }
    }
}