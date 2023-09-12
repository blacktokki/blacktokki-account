package com.example.account.domain.account.repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.example.account.core.dao.SpecificationExecutor;
import com.example.account.core.dto.BaseUserDto;
import com.example.account.domain.account.entity.Group;
import com.example.account.domain.account.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, SpecificationExecutor<User>{
    User findByUsername(String username);

    @Override
    default Predicate toPredicate(String key, Object value, Root<User> root, CriteriaBuilder builder){
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
