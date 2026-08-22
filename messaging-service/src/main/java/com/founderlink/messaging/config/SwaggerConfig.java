package com.founderlink.messaging.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI messagingServiceOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Messaging Service API")
                .description("Direct messaging — FounderLink")
                .version("1.0.0"));
    }
}
