package com.example.account.domain.account.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class TokenDto implements Serializable{
    private String token;
}
