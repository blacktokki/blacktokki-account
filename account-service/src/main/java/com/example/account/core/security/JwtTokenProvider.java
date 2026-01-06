package com.example.account.core.security;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 유효시간 30분
    private long tokenValidTime = 30 * 60 * 1000L;

    // 토큰 유효시간 (otp 포함)
    private long otpTokenValidTime = 30 * 1000L;

    // refresh 토큰 유효시간 7일
    private long refreshTokenValidTime = 7 * 24 * 3600 * 1000L;
    
    private final UserDetailsService userDetailsService;

    private JwtParser parser;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        parser = Jwts.parser().setSigningKey(secretKey);
        // warmup 처리
        try {
            parser.parseClaimsJws("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5ZGgwNTE1NDFAZ21haWwuY29tIiwib3JpZ19pYXQiOjE3NjIwODEwMjA2NjQsImlhdCI6MTc2MjA4MzU0NSwiZXhwIjoxNzYyMDg1MzQ1fQ.BZ7I_OsbcW12tTTF6bcrLXWpraih4hyA1mp54ddDglY");
        } catch (Exception e) {
        }
        try {
            userDetailsService.loadUserByUsername("");
        } catch (Exception e) {
        }
    }

    // JWT 토큰 생성 
    public String createToken(String username, List<?> roles) {
        Claims claims = Jwts.claims().setSubject(username); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Date now = new Date();
        Boolean hasOtpOnce = roles != null && roles.contains("otp_once");
        claims.put("roles", roles);
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + (hasOtpOnce ? otpTokenValidTime : tokenValidTime))) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘과
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = parser.parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String subject = parser.parseClaimsJws(token).getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String createRefreshToken(Claims claims, Boolean resetRoles) {
        long iat = claims.getIssuedAt().getTime();
        Date now = new Date();
        Date expire = new Date(iat + refreshTokenValidTime);
        List<?> roles = new ArrayList<>(Optional.ofNullable((List<?>)claims.get("roles", List.class)).orElse(List.of()));
        if (roles.contains("otp_once")) {
            roles.remove("otp_once");
        }
        if (now.before(expire)){
            return createToken(claims.getSubject(), Optional.ofNullable(resetRoles).orElse(false) ? null : roles);
        }
        return "";
    }

    public String createRefreshToken(String token, Boolean resetRoles) {
        try {
            Claims claims = parser.parseClaimsJws(token).getBody();
            return createRefreshToken(claims, resetRoles);
        } catch (ExpiredJwtException e) {
            return createRefreshToken(e.getClaims(), resetRoles);
        } catch (Exception e) {
        }
        return "";
    }
}