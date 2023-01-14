package com.example.account.domain.account.service;

import com.example.account.domain.account.dto.StorageDto;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final String BUCKET_NAME = "blacktokki-storage";
    private final Storage storage;

    public Resource download(String scope, String id, String fileName) {
        Blob blob = storage.get(BUCKET_NAME, toFilename(scope, id, fileName));
        return new ByteArrayResource(blob.getContent());
    }

    public List<StorageDto> upload(String scope, String id, MultipartFile[] files){
        return Arrays.asList(files).stream().map(file->{
            return upload(scope, id, file);
        }).collect(Collectors.toList());
    }

    public StorageDto upload(String scope, String id, MultipartFile file){
        String originalFileName = file.getOriginalFilename();
        Path path = new File(originalFileName).toPath();
        StorageDto dto = new StorageDto();
        try {
            String contentType = Files.probeContentType(path);
            Bucket bucket = storage.get(BUCKET_NAME);
            Blob blob = bucket.create(toFilename(scope, id, originalFileName), file.getInputStream(), contentType);
            dto.setFileName(blob.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dto;
    }

    private String toFilename(String scope, String id, String fileName){
        return Paths.get(scope, id, fileName).toString();
    }
}