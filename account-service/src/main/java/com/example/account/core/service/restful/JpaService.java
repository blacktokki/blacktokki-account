package com.example.account.core.service.restful;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;

@Getter
public abstract class JpaService<T, E, ID> extends JpaQueryService<T, E, ID> implements CommandService<T, ID>{
    @Override
    @Transactional
    public T update(ID id, T updated){
        T dto = get(id);
        try {
            BeanUtils.copyProperties(updated, dto);
        }
        catch (Exception e) {
        }
        E saved = getRepository().save(toEntity(dto));
        return toDto(saved);
    }

    @Override
    @Transactional
    public T bulkUpdateFields(List<ID> ids, T updated) {
        List<E> entityList = getRepository().findAllById(ids);
        for (E entity: entityList){
            getNotNullModelMapper().map(updated, entity);
        }
        getRepository().saveAll(entityList);
        return updated;
    }

    @Override
    @Transactional
    public T create(T newDomain){
        return toDto(getRepository().save(toEntity(newDomain)));
    }

    @Override
    @Transactional
    public void delete(ID id){
        getRepository().deleteById(id);
    }

    @Override
    @Transactional
    public void bulkDelete(List<ID> ids) {
        getRepository().deleteAllById(ids);  
    }
}