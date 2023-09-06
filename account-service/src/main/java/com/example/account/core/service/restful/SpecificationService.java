package com.example.account.core.service.restful;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.example.account.core.dto.PageResponseDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SpecificationService<T, E> extends QueryService<T>, PredicateService<Predicate>{
    public JpaSpecificationExecutor<E> getExecutor();

    public T toDto(E t);

    @Override
    default PageResponseDto<T> getPage(Object param, Pageable pageable){
        Page<E> result = getExecutor().findAll(toSpecification(param), pageable);
        Page<T> mappedResult = result.map((data)->toDto(data));
        return new PageResponseDto<T>(mappedResult);
    }

    @Override
    default List<T> getList(Object param, Sort sort){
        List<E> result = getExecutor().findAll(toSpecification(param), sort);
        return result.stream().map((data)->toDto(data)).collect(Collectors.toList());
    }

    default Predicate toPredicate(String key, Object value, Root<E> root, CriteriaBuilder builder){
        if (value == null){
            return null;
        }
        return builder.equal(root.get(key), value);
    }

    default Specification<E> toSpecification(Object param){
        return new Specification<E>() {
            @Override
            public Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate[] predicates;
                try {
                    predicates = toPredicates(param, (k, v)->SpecificationService.this.toPredicate(k, v, root, criteriaBuilder)).toArray(Predicate[]::new);
                } catch (IntrospectionException e) {
                    predicates = new Predicate[]{};
                }
                if (predicates.length > 0){
                    return criteriaBuilder.and(predicates);
                }
                return null;
            }
        };
    }
}
