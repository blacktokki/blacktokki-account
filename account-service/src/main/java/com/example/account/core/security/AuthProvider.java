package com.example.account.core.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import com.example.account.core.dto.AuthenticateDto;
import com.example.account.core.dto.BaseUserDto;
import com.example.account.core.entity.AbstractUser;
import com.example.account.core.service.AuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component("authProvider")
public class AuthProvider implements AuthenticationProvider  {

    @Autowired(required = false)
    private AuthenticationService service;
    
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();
        Boolean isGuestLogin = username.endsWith(".guest") && password.equals("guest");
        Boolean isGuestCreate = username.equals("guest") && password.equals("guest");
        AuthenticateDto authenticateDto = new AuthenticateDto(username, password);
        AbstractUser user = service.findByUsername(username);
        if (isGuestLogin){
            String guestDomain = createGuestDomain();
            if (user == null || !username.split("@")[1].equals(guestDomain)){
                System.out.println("invalid guest");
                throw new UsernameNotFoundException("가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.");
            }
        }
        else if (isGuestCreate){
            WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
            String sessionId = details.getSessionId().substring(0, 8);
            String guestDomain = createGuestDomain();
            String guestUsername = sessionId + "@" + guestDomain;
            user =  service.findByUsername(guestUsername);
            if(guestUsername.length() >0 && user == null){
                authenticateDto = new AuthenticateDto(guestUsername, password);
                user =  service.createGuestUser(guestUsername);
            }  
        }
        System.out.println(user.getPassword());
        System.out.println(authenticateDto.getPassword());
        // id에 맞는 user가 없거나 비밀번호가 맞지 않는 경우.
        if (user == null || !user.getPassword().equals(authenticateDto.getPassword())) {
        	System.out.println("notexist");
        	throw new UsernameNotFoundException("가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.");
        }
        
        // if (!details.equals()) {
        // 	throw new BadCredentialsException("잘못된 자동입력 방지문자 입니다.");
        // }

        List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
        
        // 로그인한 계정에게 권한 부여
        grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_USER"));
        // if (user.getIsAdmin()) {
        //     grantedAuthorityList.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        // }
 
        // 로그인 성공시 로그인 사용자 정보 반환
        BaseUserDto baseUserDto = new BaseUserDto();
        baseUserDto.setId(user.getId());
        baseUserDto.setUsername(user.getUsername());
        baseUserDto.setIsAdmin(user.getIsAdmin());
        baseUserDto.setIsGuest(user.getIsGuest());
        return new UsernamePasswordAuthenticationToken(baseUserDto, user.getPassword(),  grantedAuthorityList);
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private String createGuestDomain(){
            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
            String address = Optional.ofNullable(request.getHeader("x-forwarded-for")).orElse(request.getRemoteAddr());
            String userAgent = request.getHeader("user-agent");
            String guestDomain = "";
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                String hash = (new BigInteger(1, md.digest(address.getBytes())).add(
                    new BigInteger(1,md.digest(userAgent.getBytes())))).toString(36);
                guestDomain = hash.substring(hash.length() - 12) + ".guest";
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return guestDomain;
    }
 
}
