package com.example.account.core.dao;

import java.util.Optional;

import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparablePath;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.querydsl.SimpleEntityPathResolver;
import org.springframework.data.support.PageableExecutionUtils;

public interface QuerydslExecutor<T> extends QueryExecutor<Object, Predicate> {
    default JPAQueryFactory getFactory() {
        return null;
    }

    default Class<?> getEntityClass() {
        return ResolvableType.forClass(QuerydslExecutor.class, getClass()).getGeneric(0).resolve();
    }

    @Override
    default Page<? extends Object> findAll(Object param, Pageable pageable) {
        EntityPathBase<?> root = getEntityPathBase();
        JPAQuery<?> query = createQuery(param).offset(pageable.getOffset()).limit(pageable.getPageSize());
        JPAQuery<?> countQuery = getFactory().select(root.count()).from(root);
        addOrderBy(pageable.getSort(), query);
        return PageableExecutionUtils.getPage(query.fetch(), pageable, () -> (Long) countQuery.fetchOne());
    }

    @Override
    default Iterable<? extends Object> findAll(Object param, Sort sort) {
        JPAQuery<?> query = createQuery(param);
        addOrderBy(sort, query);
        return query.fetch();
    }

    @Override
    default Optional<? extends Object> findOne(Object param) {
        return Optional.ofNullable(createQuery(param).limit(2).fetchOne());
    }

    default EntityPathBase<?> getEntityPathBase() {
        return (EntityPathBase<?>) SimpleEntityPathResolver.INSTANCE.createPath(getEntityClass());
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

    default JPAQuery<?> createQuery(Object param) {
        EntityPathBase<?> root = getEntityPathBase();
        Predicate[] predicates = toPredicates(param, QuerydslExecutor.this::toPredicate).toArray(Predicate[]::new);
        return getFactory().selectFrom(root).where(predicates);
    }
}