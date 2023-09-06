package com.example.account.core.service.restful;

import java.util.List;

public interface RestfulService<T, ID> extends QueryService<T>{
    public T get(ID id);

    public T update(ID id, T updated);

    public T bulkUpdateFields(List<ID> ids, T updated);

    public T create(T newDomain);

    public void delete(ID id);

    public void bulkDelete(List<ID> ids);
}