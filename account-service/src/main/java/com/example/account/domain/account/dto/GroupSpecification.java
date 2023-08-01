package com.example.account.domain.account.dto;

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
    protected Predicate[] toPredicates(Root<Group> root, CriteriaBuilder builder){
        return new Predicate[]{
            toPredicateField("rootId", rootId, root, builder)
        };
    }
}
