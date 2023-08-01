package com.example.account.domain.account.dto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.security.core.context.SecurityContextHolder;

import com.example.account.core.dto.GenericSpecification;
import com.example.account.core.dto.BaseUserDto;
import com.example.account.domain.account.entity.Group;
import com.example.account.domain.account.entity.User;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserSpecification extends GenericSpecification<User>{
    private String name;
    private String username;
    private Long groupId;
    private Boolean self;

    @Override
    protected Predicate[] toPredicates(Root<User> root, CriteriaBuilder builder){
        Join<User, Group> g = root.join("groupList", JoinType.LEFT);
        if(self != null && self){
            this.username = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        }
        return new Predicate[]{
            toPredicateField("username", username, root, builder),
            toPredicateField("name", name, root, builder),
            toPredicateField("id", groupId, g, builder)
        };
    }
}
