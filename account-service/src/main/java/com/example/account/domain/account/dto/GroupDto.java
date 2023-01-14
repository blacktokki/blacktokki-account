package com.example.account.domain.account.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class GroupDto implements Serializable{
    protected Long id;
    protected String name;
    protected Long rootId;
    protected Long parentId;
    protected String imageUrl;
}
