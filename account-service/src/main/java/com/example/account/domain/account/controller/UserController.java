package com.example.account.domain.account.controller;

import com.example.account.core.dto.BaseUserDto;
import com.example.account.core.security.JwtTokenProvider;
import com.example.account.domain.account.dto.TokenDto;
import com.example.account.domain.account.dto.UserDto;
import com.example.account.domain.account.dto.UserQueryParam;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.core.controller.RestfulController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController extends RestfulController<UserDto, UserQueryParam, Long>{
    private final JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/token/refresh")
    public String refreshToken(@RequestBody TokenDto tokenDto){
        return jwtTokenProvider.createRefreshToken(tokenDto.getToken());
    }
}