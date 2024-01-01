package com.example.account.domain.account.dto;


import lombok.Data;

@Data
public class UserQueryParam{
    private String name;
    private String username;
    private Long groupId;
    private Boolean self;
}
