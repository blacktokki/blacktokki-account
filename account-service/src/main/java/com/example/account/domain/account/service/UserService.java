package com.example.account.domain.account.service;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.example.account.core.dto.AuthenticateDto;
import com.example.account.core.dto.BaseUserDto;
import com.example.account.core.service.CustomUserDetailsService;
import com.example.account.core.service.restful.RestfulService;
import com.example.account.domain.account.dto.UserDto;
import com.example.account.domain.account.dto.UserQueryParam;
import com.example.account.domain.account.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService extends RestfulService<UserDto, User, Long> implements CustomUserDetailsService{
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticateDto loadUserByUsername(String username){
        UserQueryParam userSpecification = new UserQueryParam();
        userSpecification.setUsername(username);
        User user = getExecutor().findOne(toSpecification(userSpecification)).orElse(null);
        return user != null ? getModelMapper().map(user, AuthenticateDto.class) : null;
    }
    
    @Override
    @Transactional
    public AuthenticateDto createGuestUser(String username){
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("guest"));
        user.setIsGuest(true);
        user.setIsAdmin(true);
        user.setName("Guest" + new BigInteger(1, username.getBytes()).toString().substring(0, 4));
        user = getRepository().save(user);
        return getModelMapper().map(user, AuthenticateDto.class);
    }

    @Override
    @Transactional
    public UserDto create(UserDto newUser) {
        String password = newUser.getPassword();
        newUser.setPassword(passwordEncoder.encode(password));
        UserDto user = super.create(newUser);
        newUser.setPassword(password);
        return user;
    }

    @Override
    @Transactional
    public UserDto bulkUpdateFields(List<Long> ids, UserDto updated) {
        String password = updated.getPassword();
        if(password!=null && password.length() > 0){
            updated.setPassword(passwordEncoder.encode(password));
        }
        UserDto result = super.bulkUpdateFields(ids, updated);
        updated.setPassword(password);
        return result;
    }

    @Override
    public Predicate toPredicate(String key, Object value, Root<User> root, CriteriaBuilder builder){
        if (value == null){
            return null;
        }
        if (key.equals("self") && (Boolean)value){
            String username = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            return builder.equal(root.get("username"), username);
        }
        return builder.equal(root.get(key), value);
    }
}
