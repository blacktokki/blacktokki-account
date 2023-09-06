package com.example.account.domain.account.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserSpecification implements Serializable{
    private String name;
    private String username;
    private Long groupId;
    private Boolean self;
}
