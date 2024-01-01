package com.example.account.domain.account.service;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.example.account.core.dto.AuthenticateDto;
import com.example.account.core.dto.BaseUserDto;
import com.example.account.core.service.CustomUserDetailsService;
import com.example.account.core.service.restful.RestfulService;
import com.example.account.domain.account.dto.UserDto;
import com.example.account.domain.account.dto.UserQueryParam;
import com.example.account.domain.account.entity.Group;
import com.example.account.domain.account.entity.Membership;
import com.example.account.domain.account.entity.User;
import com.example.account.domain.account.repository.GroupRepository;
import com.example.account.domain.account.repository.MembershipRepository;

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
    private final GroupRepository groupRepository;

    private final MembershipRepository membershipRepository;

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
        Group group = groupRepository.findByName(username);
        createMember(user.getId(), group, username);
        return getModelMapper().map(user, AuthenticateDto.class);
    }

    @Override
    @Transactional
    public UserDto create(UserDto newUser) {
        String password = newUser.getPassword();
        newUser.setPassword(passwordEncoder.encode(password));
        UserDto user = super.create(newUser);
        newUser.setPassword(password);
        String defaultGroupName = user.getUsername() + "'s group";
        if (newUser.getInviteGroupId() != null){
            Group group = groupRepository.getById(newUser.getInviteGroupId());
            createMember(user.getId(), group, defaultGroupName);
        }
        else{
            createMember(user.getId(), null, defaultGroupName);
        }
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

    private void createMember(Long userId, Group group, String groupName){
        if (group == null){
            group = new Group();
            group.setName(groupName);
            group = groupRepository.save(group);
        }
        Membership membership = new Membership();
        membership.setUserId(userId);
        membership.setGroupId(group.getId());
        membershipRepository.save(membership);
    }

    @Override
    public Predicate toPredicate(String key, Object value, Root<User> root, CriteriaBuilder builder){
        if (value == null){
            return null;
        }
        Join<User, Group> g = root.join("groupList", JoinType.LEFT);
        if (key.equals("self") && (Boolean)value){
            String username = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
            return builder.equal(root.get(key), username);
        }
        if (key.equals("groupId")){
            return builder.equal(g.get(key), value);
        }
        return builder.equal(root.get(key), value);
    }
}
