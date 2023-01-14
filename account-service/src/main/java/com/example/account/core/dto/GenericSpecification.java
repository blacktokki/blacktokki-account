package com.example.account.core.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

public abstract class GenericSpecification<T> implements Specification<T>{
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<Predicate>();
        toPredicate(predicates, root, builder);
        if (predicates.size() > 0){
            return builder.and(predicates.toArray(new Predicate[0]));
        }
        return null;
    }

    abstract protected void toPredicate(List<Predicate> predicates, Root<T> root, CriteriaBuilder builder);

    protected void toPredicateField(List<Predicate> predicates, String key, Object value, Path<?> path, CriteriaBuilder builder){
        if (value != null){
            predicates.add(builder.equal(path.get(key), value));
        }
    }

    protected void toPredicateFieldIsNull(List<Predicate> predicates, String key, Boolean value, Path<?> path, CriteriaBuilder builder){
        if (value != null){
            if (value)
                predicates.add(builder.isNull(path.get(key)));
            else
                predicates.add(builder.isNotNull(path.get(key)));
        }
    }
}
