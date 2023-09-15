package com.example.account.core.dao;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface QueryExecutor<T, P> {
    Page<T> findAll(Object param, Pageable pageable);

    Iterable<T> findAll(Object param, Sort sort);

    Optional<T> findOne(Object param);

    default Stream<P> toPredicates(Object param, BiFunction<String, Object, P> callback) {
        try {
            return Arrays.stream(Introspector.getBeanInfo(param.getClass()).getPropertyDescriptors()).map(pd -> {
                if (pd.getReadMethod() != null && !"class".equals(pd.getName())) {
                    try {
                        return callback.apply(pd.getName(), pd.getReadMethod().invoke(param));
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }).filter(Objects::nonNull);
        } catch (IntrospectionException e) {
            return Stream.of();
        }
    }
}
