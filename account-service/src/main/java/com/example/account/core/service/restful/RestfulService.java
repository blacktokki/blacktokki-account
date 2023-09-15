package com.example.account.core.service.restful;

import lombok.Getter;

@Getter
public abstract class RestfulService<T, E, ID> extends GenericService<T, E, ID> implements JpaQueryService<T, E, ID>,  JpaCommandService<T, E, ID>{
}