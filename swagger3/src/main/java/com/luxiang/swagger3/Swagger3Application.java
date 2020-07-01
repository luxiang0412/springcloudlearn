package com.luxiang.swagger3;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Swagger3Application {

    public static void main(String[] args) {
        SpringApplication.run(Swagger3Application.class, args);
    }

    @Bean
    public OpenAPI customOpenAPI(@Value("${springdoc.version}") String appVersion) {
        return new OpenAPI().info(new Info().title("Foobar API")
                .version(appVersion)
                .description("This is a sample Foobar server created using springdocs - a library for OpenAPI 3 with spring boot.")
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0")
                        .url("http://springdoc.org")));
    }
}
