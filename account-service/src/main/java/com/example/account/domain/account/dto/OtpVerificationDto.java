package com.example.account.domain.account.dto;

import lombok.Data;

@Data
public class OtpVerificationDto {
    private String secretKey;
    private int code;
}