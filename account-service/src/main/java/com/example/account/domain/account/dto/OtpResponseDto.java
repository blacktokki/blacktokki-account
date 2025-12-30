package com.example.account.domain.account.dto;

import lombok.Data;

@Data
public class OtpResponseDto {
    private String secretKey;
    private String otpAuthUrl;
}
