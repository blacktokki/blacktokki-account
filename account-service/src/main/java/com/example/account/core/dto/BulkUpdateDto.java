package com.example.account.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkUpdateDto<T, ID> {
    private List<ID> ids;
    private T updated;
}