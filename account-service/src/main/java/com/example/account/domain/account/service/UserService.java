package com.example.account.domain.account.service;

import java.math.BigInteger;

import com.example.account.core.dto.AuthenticateDto;
import com.example.account.core.service.CustomUserDetailsService;
import com.example.account.core.service.GenericService;
import com.example.account.domain.account.dto.UserDto;
import com.example.account.domain.account.dto.UserSpecification;
import com.example.account.domain.account.entity.Group;
import com.example.account.domain.account.entity.Membership;
import com.example.account.domain.account.entity.User;
import com.example.account.domain.account.repository.GroupRepository;
import com.example.account.domain.account.repository.MembershipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends GenericService<User, UserDto, Long> implements CustomUserDetailsService{
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    MembershipRepository membershipRepository;

    PasswordEncoder passwordEncoder;
    
    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthenticateDto loadUserByUsername(String username){
        UserSpecification accountUserSpecification = new UserSpecification();
        accountUserSpecification.setUsername(username);
        User user = specificationExecutor.findOne(accountUserSpecification).orElse(null);
        return user != null ? modelMapper.map(user, AuthenticateDto.class) : null;
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
        user = repository.save(user);
        Group group = groupRepository.findByName(username);
        createMember(user, group, username);
        return modelMapper.map(user, AuthenticateDto.class);
    }

    @Override
    @Transactional
    public UserDto create(UserDto newUser) {
        User user = toEntity(newUser);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = repository.save(user);
        String defaultGroupName = user.getUsername() + "'s group";
        if (newUser.getInviteGroupId() != null){
            Group group = groupRepository.getById(newUser.getInviteGroupId());
            createMember(user, group, defaultGroupName);
        }
        else{
            createMember(user, null, defaultGroupName);
        }
        return toDto(user);
    }

    private void createMember(User user, Group group, String groupName){
        if (group == null){
            group = new Group();
            group.setName(groupName);
            group = groupRepository.save(group);
        }
        Membership membership = new Membership();
        membership.setUserId(user.getId());
        membership.setGroupId(group.getId());
        membershipRepository.save(membership);
    }
}
