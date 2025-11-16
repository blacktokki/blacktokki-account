package com.example.account.core.config;


import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.account.domain.account.dto.UserQueryParam;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MapperConfig {
    private final CustomJsr310Module customJsr310Module = new CustomJsr310Module();

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        // .setCollectionsMergeEnabled(false)
        modelMapper.registerModule(customJsr310Module);
        return modelMapper;
    }

    @Bean
    public ModelMapper notNullModelMapper() {
        ModelMapper notNullModelMapper = new ModelMapper();
        notNullModelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        // .setCollectionsMergeEnabled(false)
        notNullModelMapper.registerModule(customJsr310Module);
        return notNullModelMapper;
    }

    @Bean
    public ApplicationListener<ApplicationReadyEvent> dtoWarmupListener() {
        return new ApplicationListener<ApplicationReadyEvent>() {
            @Override
            public void onApplicationEvent(ApplicationReadyEvent event) {
                long beforeTime = System.currentTimeMillis();
                try {
                    BeanUtils.getPropertyDescriptors( UserQueryParam.class);
                } catch (Exception e) {
                }
                 log.info(("1: " + (System.currentTimeMillis() - beforeTime)));
            }
        };
    }
}
