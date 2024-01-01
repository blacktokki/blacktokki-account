package com.example.account.domain.account.dto;


import lombok.Data;

@Data
public class GroupDto{
    protected Long id;
    protected String name;
    protected Long rootId;
    protected Long parentId;
    protected String imageUrl;
}
