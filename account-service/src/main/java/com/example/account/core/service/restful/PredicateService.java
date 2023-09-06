package com.example.account.core.service.restful;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public interface PredicateService<T> {
    default T toPredicate(Object param, PropertyDescriptor pd, BiFunction<String, Object, T> callback){
        if (pd.getReadMethod() != null && !"class".equals(pd.getName())){
            try {
                return callback.apply(pd.getName(), pd.getReadMethod().invoke(param));
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    default Stream<T> toPredicates(Object param, BiFunction<String, Object, T> callback) throws IntrospectionException{
        return Arrays.stream(Introspector.getBeanInfo(param.getClass()).getPropertyDescriptors()).map(
            pd->PredicateService.this.toPredicate(param, pd, callback)
        ).filter(Objects::nonNull);
    }
}
