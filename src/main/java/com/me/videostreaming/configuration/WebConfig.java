package com.me.videostreaming.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/authenticate").allowCredentials(true).allowedOrigins("http://localhost:63342");
            registry.addMapping("/users").allowCredentials(true).allowedOrigins("http://localhost:63342");
    }
}

