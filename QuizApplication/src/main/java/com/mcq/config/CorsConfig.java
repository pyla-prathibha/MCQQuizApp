package com.mcq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")  // Allow CORS for all API endpoints
                .allowedOrigins("http://localhost:3000")  // Allow requests from your frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // Allow methods
                .allowedHeaders("*");  // Allow all headers
    }
}
