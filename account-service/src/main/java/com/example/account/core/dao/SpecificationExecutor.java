package com.example.account.core.dao;

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SpecificationExecutor<T> extends JpaSpecificationExecutor<T>, QueryExecutor<T, Predicate>{
    @Override
    default Page<? extends T> findAll(Object param, Pageable pageable) {
        return findAll(toSpecification(param), pageable);
    }

    @Override
    default Iterable<? extends T> findAll(Object param, Sort sort) {
        return findAll(toSpecification(param), sort);
    }

    @Override
    default Optional<? extends T> findOne(Object param) {
        return findOne(toSpecification(param));
    }


    
    default Predicate toPredicate(String key, Object value, Root<T> root, CriteriaBuilder builder){
        if (value == null){
            return null;
        }
        return builder.equal(root.get(key), value);
    }

    default Specification<T> toSpecification(Object param){
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates = toPredicates(param, (k, v)->SpecificationExecutor.this.toPredicate(k, v, root, criteriaBuilder)).toArray(Predicate[]::new);
                if (predicates.length > 0){
                    return criteriaBuilder.and(predicates);
                }
                return null;
            }
        };
    }
}
