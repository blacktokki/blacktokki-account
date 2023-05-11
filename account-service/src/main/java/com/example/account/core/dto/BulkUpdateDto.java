package com.example.account.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkUpdateDto<T, ID> implements Serializable {
    private List<ID> ids;
    private T updated;
}