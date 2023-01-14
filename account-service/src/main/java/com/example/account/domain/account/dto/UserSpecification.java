package com.example.account.domain.account.dto;

import java.util.List;

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
    protected void toPredicate(List<Predicate> predicates, Root<User> root, CriteriaBuilder builder){
        Join<User, Group> g = root.join("groupList", JoinType.LEFT);
        if(self != null && self){
            this.username = ((BaseUserDto)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        }
        toPredicateField(predicates, "username", username, root, builder);
        toPredicateField(predicates, "name", name, root, builder);
        toPredicateField(predicates, "id", groupId, g, builder);
    }
}
