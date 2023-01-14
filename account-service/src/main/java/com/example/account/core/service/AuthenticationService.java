package com.example.account.core.service;

import com.example.account.core.entity.AbstractUser;

public interface AuthenticationService {
    AbstractUser findByUsername(String username);
    AbstractUser createGuestUser(String username);
}
