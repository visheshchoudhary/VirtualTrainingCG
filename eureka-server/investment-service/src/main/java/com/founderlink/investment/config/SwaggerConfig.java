package com.founderlink.investment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI investmentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Investment Service API")
                        .description("Investment tracking — FounderLink")
                        .version("1.0.0"));
    }
}
