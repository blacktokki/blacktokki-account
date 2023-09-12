package com.example.account.domain.account.repository;

import com.example.account.core.dao.SpecificationExecutor;
import com.example.account.domain.account.entity.Group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long>, SpecificationExecutor<Group>{
    Group findByName(String name);
}
