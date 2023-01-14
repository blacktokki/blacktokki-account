package com.example.account.domain.account.controller;

import java.util.List;

import com.example.account.domain.account.dto.StorageDto;
import com.example.account.domain.account.service.StorageService;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storage")
public class StorageController {
    private final StorageService storageService;

    @GetMapping("/{scope}/{id}")
    public ResponseEntity<Resource> download(@PathVariable String scope, @PathVariable String id, @RequestParam String fileName){
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return ResponseEntity.ok().headers(headers).body(storageService.download(scope, id, fileName));
    }

    @PostMapping(path="/{scope}/{id}", consumes={MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<List<StorageDto>> upload(@PathVariable String scope, @PathVariable String id, @RequestPart MultipartFile[] files){
        return ResponseEntity.ok(storageService.upload(scope, id, files));
    }
}