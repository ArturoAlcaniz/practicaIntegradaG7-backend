package com.practicaintegradag7.config;

// Para ver la documentacion: http://localhost:8080/swagger-ui.html

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration @EnableSwagger2
public class SwaggerConfig {

            @Bean
            public Docket api() {
                        return new Docket(DocumentationType.SWAGGER_2)
                                    .select()
                                    .apis(
                                            RequestHandlerSelectors
                                            .basePackage("com.practicaintegradag7.controllers"))
                                    .paths(PathSelectors.any())
                                    .build()
                                    .apiInfo(getApiInfo());
                }
            
            @SuppressWarnings("deprecation")
			private ApiInfo getApiInfo() {
        		return new ApiInfo(
        				"SIGEVA API",
        				"Documentación de la API de SIGEVA realizada con Swagger",
        				"1.0",
        				null, null, null, null)
        				;
        	}
}
