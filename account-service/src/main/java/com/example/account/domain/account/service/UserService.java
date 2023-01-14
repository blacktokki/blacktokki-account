package com.example.account.domain.account.service;

import com.example.account.core.entity.AbstractUser;
import com.example.account.core.service.AuthenticationService;
import com.example.account.core.service.GenericService;
import com.example.account.domain.account.dto.UserDto;
import com.example.account.domain.account.dto.UserSpecification;
import com.example.account.domain.account.entity.Group;
import com.example.account.domain.account.entity.Membership;
import com.example.account.domain.account.entity.User;
import com.example.account.domain.account.repository.GroupRepository;
import com.example.account.domain.account.repository.MembershipRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService extends GenericService<User, UserDto, Long> implements AuthenticationService{
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    MembershipRepository membershipRepository;

    @Override
    public User findByUsername(String username){
        UserSpecification accountUserSpecification = new UserSpecification();
        accountUserSpecification.setUsername(username);
        return specificationExecutor.findOne(accountUserSpecification).orElse(null);
    }
    
    @Override
    @Transactional
    public AbstractUser createGuestUser(String username){
        String[] split = username.split("@"); 
        User user = new User();
        user.setUsername(username);
        user.setPassword("guest");
        user.setIsGuest(true);
        user.setIsAdmin(true);
        user.setName("guest:" + split[0]);
        user = repository.save(user);
        createMember(user, split[1]);
        return user;
    }

    @Override
    @Transactional
    public UserDto create(UserDto newDomain) {
        User user = toEntity(newDomain);
        user = repository.save(user);
        createMember(user, user.getUsername().split("@")[1]);
        return toDto(user);
    }

    private void createMember(User user, String groupName){
        Group group = groupRepository.findByName(groupName);
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
