package com.example.account.core.dto;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public abstract class GenericSpecification<T> implements Specification<T>{
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate[] predicates = toPredicates(root, builder);
        if (predicates.length > 0){
            return builder.and(predicates);
        }
        return null;
    }

    abstract protected Predicate[] toPredicates(Root<T> root, CriteriaBuilder builder);

    protected Predicate toPredicateField(String key, Object value, Path<?> path, CriteriaBuilder builder){
        if (value != null){
            return builder.equal(path.get(key), value);
        }
        return null;
    }

    protected Predicate toPredicateFieldIsNull(String key, Boolean value, Path<?> path, CriteriaBuilder builder){
        if (value != null){
            if (value)
                return builder.isNull(path.get(key));
            else
                return builder.isNotNull(path.get(key));
        }
        return null;
    }
}
