package com.example.account.domain.account.controller;

import com.example.account.core.security.JwtTokenProvider;
import com.example.account.core.service.CustomUserDetailsService;
import com.example.account.domain.account.dto.TokenDto;
import com.example.account.domain.account.dto.UserDto;
import com.example.account.domain.account.dto.UserQueryParam;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.account.core.controller.RestfulController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController extends RestfulController<UserDto, UserQueryParam, Long>{
    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserDetailsService service;
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @PostMapping("/token/refresh")
    public String refreshToken(@RequestBody TokenDto tokenDto){
        return jwtTokenProvider.createRefreshToken(tokenDto.getToken());
    }

    @PostMapping("/google")
    public ResponseEntity<String> loginWithGoogle(@RequestBody Map<String, String> body) {
        GoogleIdToken.Payload payload = verifyIdToken(body.get("token"));
        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = payload.getEmail();
        UserDetails user =  service.loadUserByUsername(username);
        if(user == null){
            user = service.createOauthUser(username, (String) payload.get("name"));
        } 
        System.out.println(username);
        return ResponseEntity.ok(jwtTokenProvider.createToken(username, null, null));
    }

    private GoogleIdToken.Payload verifyIdToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}