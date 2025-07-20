package com.example.account.core.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomUserDetailsService extends UserDetailsService{
    UserDetails createGuestUser(String username);

    UserDetails createOauthUser(String username, String name);
}
