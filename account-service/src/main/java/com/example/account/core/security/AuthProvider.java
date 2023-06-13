package com.example.account.core.security;

import java.util.Optional;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import com.example.account.core.service.CustomUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.authentication.AuthenticationProvider;
// import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component("authProvider")
public class AuthProvider implements AuthenticationProvider  {

    @Autowired(required = false)
    private CustomUserDetailsService service;
    
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String)authentication.getCredentials();
        Boolean isGuestLogin = username.endsWith(".guest") && password.equals("guest");
        Boolean isGuestCreate = username.equals("guest") && password.equals("guest");
        UserDetails user = service.loadUserByUsername(username);
        if (isGuestLogin){
            String guestDomain = createGuestDomain();
            if (user == null || !username.split("@")[1].equals(guestDomain)){
                System.out.println("invalid guest");
                throw new UsernameNotFoundException("Unregistered user or incorrect password.");
            }
        }
        else if (isGuestCreate){
            WebAuthenticationDetails details = (WebAuthenticationDetails)authentication.getDetails();
            String sessionId = details.getSessionId().substring(0, 8);
            String guestDomain = createGuestDomain();
            username = sessionId + "@" + guestDomain;
            user =  service.loadUserByUsername(username);
            if(user == null){
                user = service.createGuestUser(username);
            }  
        }
        System.out.println(user.getPassword());
        System.out.println(password);
        // id에 맞는 user가 없거나 비밀번호가 맞지 않는 경우.
        if (user == null || !user.getPassword().equals(password)) {
        	System.out.println("notexist");
        	throw new UsernameNotFoundException("Unregistered user or incorrect password.");
        }    
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
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
