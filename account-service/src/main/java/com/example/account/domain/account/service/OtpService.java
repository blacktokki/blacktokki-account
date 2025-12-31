package com.example.account.domain.account.service;

import com.example.account.core.dto.BaseUserDto;
import com.example.account.core.service.UtilService;
import com.example.account.core.service.VisitService;
import com.example.account.domain.account.dto.OtpResponseDto;
import com.example.account.domain.account.dto.OtpVerificationDto;
import com.example.account.domain.account.entity.User;
import com.example.account.domain.account.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import lombok.RequiredArgsConstructor;

import java.util.Base64;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpService implements VisitService {
    @Value("${jwt.secret}")
    private String secretKey;

    private final UtilService utilService;

    private final UserRepository userRepository;

    private final String ISSUR = "Blacktokki Account";

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    private final String algorithm = "AES";

    private SecretKeySpec keySpec;

    @PostConstruct
    private void init(){
        keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
    }

    private String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("암호화 중 오류 발생", e);
        }
    }

    // 복호화: Base64 암호문 -> 평문
    private String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("복호화 중 오류 발생", e);
        }
    }

    private User getUser() {
        BaseUserDto baseUserDto = utilService.getUser();
        return userRepository.getById(baseUserDto.getId());
    }

    public OtpResponseDto getQrCodeUrl() {
        User user =  getUser();
        OtpResponseDto response = new OtpResponseDto();
        if (user.getOtpSecret() == null) {
            GoogleAuthenticatorKey key = gAuth.createCredentials();
            String qrUrl = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(ISSUR, user.getUsername(), key);
            String userKey = key.getKey();
            response.setSecretKey(userKey);
            response.setOtpAuthUrl(qrUrl);
        }
        return response;
    }

    public boolean verifyOtp(OtpVerificationDto request) {
        User user =  getUser();
        if (request.getSecretKey() != null) {
            Boolean result = gAuth.authorize(request.getSecretKey(), request.getCode());
            if (result) {
                userRepository.updateOtpStatus(user.getId(), request.getSecretKey(), false);
            }
            return result;
        }
        else {
            return gAuth.authorize(decrypt(user.getOtpSecret()), request.getCode());
        }
    }

    public void delete() {
        User user =  getUser();
        userRepository.updateOtpStatus(user.getId(), user.getOtpSecret(), true);
    }

    @Override
    public void visit(String username) {
        User user = userRepository.findByUsername(username);
        if (user.getOtpDeletionRequested()) {
            userRepository.updateOtpStatus(user.getId(), null, null);
        }
    }
}