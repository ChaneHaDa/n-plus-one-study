package com.example.nplusone.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("N+1 문제 학습용 API")
                        .description("JPA N+1 문제를 실습해볼 수 있는 API입니다")
                        .version("1.0.0"));
    }
}