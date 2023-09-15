package com.example.account.core.controller;

import com.example.account.core.service.restful.RestfulService;
import com.example.account.core.dao.QueryExecutor;
import com.example.account.core.service.restful.CommandService;
import com.example.account.core.service.restful.QueryService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.Getter;

public abstract class JpaController<T, E, Q, ID> extends RestfulController<T, Q, ID>{
    @Autowired
    private JpaRepository<E, ID> repository;

    @Autowired
    private QueryExecutor<E, ?> executor;

    @Autowired
    @Qualifier("modelMapper")
    private ModelMapper modelMapper;

    @Autowired
    @Qualifier("notNullModelMapper")
    private ModelMapper notNullModelMapper;

    private final RestfulService<T, E, ID> service = new RestfulService<T, E, ID>(){
        @Getter
        private final JpaRepository<E, ID> repository = JpaController.this.repository;

        @Getter
        private final QueryExecutor<E, ?> executor = JpaController.this.executor;

        @Getter
        private final ModelMapper modelMapper = JpaController.this.modelMapper;

        @Getter
        private final ModelMapper notNModelMapper = JpaController.this.notNullModelMapper;

        @Getter
        private final Class<T> dtoClass = this.getGenericClass(0);

        @Getter
        private final Class<E> entityClass = this.getGenericClass(1);
    };

    @Getter
    private final CommandService<T, ID>  commandService = service;

    @Getter
    private final QueryService<T, ID> queryService = service;
}