package com.example.account.core.controller;

import java.util.List;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.annotation.PostConstruct;

import com.example.account.core.dto.BulkUpdateDto;
import com.example.account.core.dto.GenericSpecification;
import com.example.account.core.dto.PageResponseDto;
import com.example.account.core.service.GenericService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

public abstract class GenericController<T, B, Q extends GenericSpecification<T>, ID> {
    @Autowired(required = false)
    protected GenericService<T, B, ID> service;
    
    @Autowired(required = false)
    protected JpaRepository<T, ID> repository;

    @Autowired(required = false)
    protected JpaSpecificationExecutor<T> specificationExecutor;

    private Type[] typeList = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();

    @PostConstruct
    public void postConstruct(){
        if (service==null && repository!=null){
            service = new GenericService<T, B, ID>(){
                {
                    this.repository = GenericController.this.repository;
                    this.specificationExecutor = GenericController.this.specificationExecutor;
                    this.typeList = GenericController.this.typeList;
                }
            };
            service.postConstruct();
        }
    }

    @GetMapping("")
    public ResponseEntity<PageResponseDto<B>> getPage(Pageable pageable, Q queryParam){
        return ResponseEntity.ok(service.getPage(pageable, queryParam));
    }

    @GetMapping("/{id}")
    public ResponseEntity<B> getOne(@PathVariable ID id){
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping("")
    public ResponseEntity<B> create(@RequestBody B created){
        return ResponseEntity.ok(service.create(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<B> update(@PathVariable ID id, @RequestBody B updated){
        return ResponseEntity.ok(service.update(id, updated));
    }

    @PatchMapping("/")
    public ResponseEntity<B> bulkUpdateFields(@RequestBody BulkUpdateDto<B, ID> updated){
        return ResponseEntity.ok(service.bulkUpdateFields(updated.getIds(), updated.getUpdated()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable ID id){
        service.delete(id);
        return ResponseEntity.ok("Ok");
    }

    @DeleteMapping("/")
    public ResponseEntity<String> delete(@RequestBody List<ID> ids){
        service.bulkDelete(ids);
        return ResponseEntity.ok("Ok");
    }
}