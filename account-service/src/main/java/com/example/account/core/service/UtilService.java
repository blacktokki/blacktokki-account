package com.example.account.core.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.account.core.dto.BaseUserDto;

@Service
public class UtilService {
    public LocalDateTime now(){
        return ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
    }

    public LocalDate today(){
        return now().toLocalDate();
    }

    public BaseUserDto getUser(){
        return (BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
