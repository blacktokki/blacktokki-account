package com.example.account.domain.account.dto;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.example.account.core.dto.GenericSpecification;
import com.example.account.domain.account.entity.Group;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class GroupSpecification extends GenericSpecification<Group>{
    private Long rootId;

    @Override
    protected void toPredicate(List<Predicate> predicates, Root<Group> root, CriteriaBuilder builder){
        toPredicateField(predicates, "rootId", rootId, root, builder);
    }
}
