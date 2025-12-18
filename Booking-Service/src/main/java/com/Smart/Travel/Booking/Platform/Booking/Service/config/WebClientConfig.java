package com.Smart.Travel.Booking.Platform.Booking.Service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient userServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("${services.user.url:http://localhost:8081}")
                .build();
    }

    @Bean
    public WebClient notificationServiceWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl("${services.notification.url:http://localhost:8086}")
                .build();
    }
}
