package com.example.account.domain.account.repository;

import com.example.account.core.dao.SpecificationExecutor;
import com.example.account.domain.account.entity.Membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Integer>, SpecificationExecutor<Membership>{

}
