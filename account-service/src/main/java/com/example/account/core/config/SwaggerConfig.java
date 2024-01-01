package com.example.account.core.config;
// import java.util.Arrays;

import java.util.List;

import com.fasterxml.classmate.TypeResolver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

// http://localhost:8080/swagger-ui/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String API_NAME = "Study API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "Study API 명세서";

    TypeResolver typeResolver = new TypeResolver();

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .alternateTypeRules(AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class),
                    typeResolver.resolve(SwaggerPageable.class)))
                .apiInfo(apiInfo())
                // .securitySchemes(Arrays.asList(new BasicAuth("basicAuth")))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(API_NAME)
                .version(API_VERSION)
                .description( API_DESCRIPTION)
                .build();
    }

    @Data
    @ApiModel
    static class SwaggerPageable {
        @ApiModelProperty(example = "0")
        private Integer page;

        @ApiModelProperty(example = "0")
        private Integer size;

        @ApiModelProperty(value = "정렬(사용법: 컬럼명,ASC|DESC)")
        private List<String> sort;
    }
}