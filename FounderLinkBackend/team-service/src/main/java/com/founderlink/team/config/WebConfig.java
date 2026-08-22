package com.founderlink.team.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS is handled centrally by the API Gateway.
// Do NOT add CORS mappings here — it causes duplicate headers that browsers reject.
@Configuration
public class WebConfig implements WebMvcConfigurer {
}
