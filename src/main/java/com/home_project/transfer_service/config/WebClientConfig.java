package com.home_project.transfer_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient accountWebClient() {
        return WebClient.builder()
                .baseUrl("http://account-service")
                .build();
    }

    @Bean
    WebClient userWebClient() {
        return WebClient.builder()
                .baseUrl("http://user-service")
                .build();
    }
}
