package com.example.account.core.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AuthenticateDto extends BaseUserDto{
    private String password;

    @Builder
    public AuthenticateDto(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
