package com.example.account.core.controller;

import java.util.List;

import com.example.account.core.dto.BulkUpdateDto;
import com.example.account.core.dto.PageResponseDto;
import com.example.account.core.service.restful.RestfulService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.Getter;

public abstract class RestfulController<T, Q, ID> {
    @Autowired(required = false)
    @Getter
    private RestfulService<T, ID> service;

    @GetMapping("")
    public ResponseEntity<PageResponseDto<T>> getPage(Pageable pageable, Q queryParam){
        return ResponseEntity.ok(getService().getPage(queryParam, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getOne(@PathVariable ID id){
        return ResponseEntity.ok(getService().get(id));
    }

    @PostMapping("")
    public ResponseEntity<T> create(@RequestBody T created){
        return ResponseEntity.ok(getService().create(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable ID id, @RequestBody T updated){
        return ResponseEntity.ok(getService().update(id, updated));
    }

    @PatchMapping("/")
    public ResponseEntity<T> bulkUpdateFields(@RequestBody BulkUpdateDto<T, ID> updated){
        return ResponseEntity.ok(getService().bulkUpdateFields(updated.getIds(), updated.getUpdated()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable ID id){
        getService().delete(id);
        return ResponseEntity.ok("Ok");
    }

    @DeleteMapping("/")
    public ResponseEntity<String> delete(@RequestBody List<ID> ids){
        getService().bulkDelete(ids);
        return ResponseEntity.ok("Ok");
    }
}