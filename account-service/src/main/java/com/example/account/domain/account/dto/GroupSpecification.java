package com.example.account.domain.account.dto;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GroupSpecification implements Serializable {
    private Long rootId;
}
