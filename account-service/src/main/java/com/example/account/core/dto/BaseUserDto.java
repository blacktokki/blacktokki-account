package com.example.account.core.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUserDto{
    protected Long id;
    protected String username;
    protected String name;
    protected Boolean isAdmin;
    protected Boolean isGuest;
    protected String imageUrl;
}
