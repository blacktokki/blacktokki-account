package com.example.account.core.controller;

import java.util.List;

import com.example.account.core.dto.BulkUpdateDto;
import com.example.account.core.dto.PageResponseDto;
import com.example.account.core.service.RestService;

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

public abstract class RestController<T, Q, ID> {
    @Autowired(required = false)
    protected RestService<T, ? super Q, ID> service;

    @GetMapping("")
    public ResponseEntity<PageResponseDto<T>> getPage(Pageable pageable, Q queryParam){
        return ResponseEntity.ok(service.getPage(pageable, queryParam));
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> getOne(@PathVariable ID id){
        return ResponseEntity.ok(service.get(id));
    }

    @PostMapping("")
    public ResponseEntity<T> create(@RequestBody T created){
        return ResponseEntity.ok(service.create(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable ID id, @RequestBody T updated){
        return ResponseEntity.ok(service.update(id, updated));
    }

    @PatchMapping("/")
    public ResponseEntity<T> bulkUpdateFields(@RequestBody BulkUpdateDto<T, ID> updated){
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