package com.example.account.core.service.restful;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCommandService<T, E, ID> extends CommandService<T, ID> {
    public JpaRepository<E, ID> getRepository();

    public ModelMapper getNotNullModelMapper();


    public T toDto(E e);

    public E toEntity(T t);

    @Override
    @Transactional
    default T update(ID id, T updated){
        T dto = toDto(getRepository().findById(id).get());
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
    default T bulkUpdateFields(List<ID> ids, T updated) {
        List<E> entityList = getRepository().findAllById(ids);
        for (E entity: entityList){
            getNotNullModelMapper().map(updated, entity);
        }
        getRepository().saveAll(entityList);
        return updated;
    }

    @Override
    @Transactional
    default T create(T newDomain){
        return toDto(getRepository().save(toEntity(newDomain)));
    }

    @Override
    @Transactional
    default void delete(ID id){
        getRepository().deleteById(id);
    }

    @Override
    @Transactional
    default void bulkDelete(List<ID> ids) {
        getRepository().deleteAllById(ids);  
    }

}
