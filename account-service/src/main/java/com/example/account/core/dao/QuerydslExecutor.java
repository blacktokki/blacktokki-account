package com.example.account.core.dao;

import java.util.Optional;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;

public interface QuerydslExecutor<T> extends QueryExecutor<T, Predicate> {
    default JPAQueryFactory getFactory() {
        return null;
    }

    default Class<T> getEntityClass() {
        return null;
    }

    @Override
    default Page<T> findAll(Object param, Pageable pageable) {
        EntityPathBase<T> root = getEntityPathBase();
        JPAQuery<T> query = createQuery(param).offset(pageable.getOffset()).limit(pageable.getPageSize());
        JPAQuery<Long> countQuery = getFactory().select(root.count()).from(root);
        addOrderBy(pageable.getSort(), query);
        return PageableExecutionUtils.getPage(query.fetch(), pageable, () -> countQuery.fetchOne());
    }

    @Override
    default Iterable<T> findAll(Object param, Sort sort) {
        JPAQuery<T> query = createQuery(param);
        addOrderBy(sort, query);
        return query.fetch();
    }

    @Override
    default Optional<T> findOne(Object param) {
        return Optional.ofNullable(createQuery(param).limit(2).fetchOne());
    }

    default EntityPathBase<T> getEntityPathBase() {
        return (EntityPathBase<T>) SimpleEntityPathResolver.INSTANCE.createPath(getEntityClass());
    }

    default void addOrderBy(Sort sort, JPAQuery<?> query) {
        for (Order order : sort) {
            ComparablePath<Comparable<?>> orderPath = Expressions.comparablePath(Comparable.class, order.getProperty());
            query.orderBy(order.isAscending() ? orderPath.asc() : orderPath.desc());
        }
    }

    default Predicate toPredicate(String key, Object value) {
        if (value != null) {
            return Expressions.predicate(Ops.EQ, Expressions.path(Object.class, getEntityPathBase(), key),
                    Expressions.constant(value));
        }
        return null;
    }

    default JPAQuery<T> createQuery(Object param) {
        EntityPathBase<T> root = getEntityPathBase();
        Predicate[] predicates = toPredicates(param, QuerydslExecutor.this::toPredicate).toArray(Predicate[]::new);
        return getFactory().selectFrom(root).where(predicates);
    }
}
