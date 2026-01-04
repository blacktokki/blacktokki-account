package com.example.account.domain.account.controller;

import com.example.account.domain.account.dto.OtpResponseDto;
import com.example.account.domain.account.dto.OtpVerificationDto;
import com.example.account.domain.account.service.OtpService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("")
    public ResponseEntity<OtpResponseDto> generateOtp() {        
        OtpResponseDto response = otpService.getQrCodeUrl();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationDto request) {
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }

    @DeleteMapping("")
    public ResponseEntity<String> delete() {
        otpService.delete();
        return ResponseEntity.status(204).build();
    }
}