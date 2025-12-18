package com.Smart.Travel.Booking.Platform.Booking.Service.service;

import com.Smart.Travel.Booking.Platform.Booking.Service.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.user.url}")
    private String userServiceUrl;

    public Mono<UserDTO> getUserById(Long userId) {
        log.info("Fetching user with id: {} from User Service", userId);
        return webClientBuilder.build()
                .get()
                .uri(userServiceUrl + "/api/users/{id}", userId)
                .retrieve()
                .bodyToMono(UserDTO.class)
                .doOnSuccess(user -> log.info("Successfully fetched user: {}", user.getEmail()))
                .doOnError(error -> log.error("Error fetching user: {}", error.getMessage()));
    }

    public Mono<Boolean> validateUser(Long userId) {
        log.info("Validating user with id: {}", userId);
        return webClientBuilder.build()
                .get()
                .uri(userServiceUrl + "/api/users/{id}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess(isValid -> log.info("User validation result: {}", isValid))
                .doOnError(error -> log.error("Error validating user: {}", error.getMessage()))
                .onErrorReturn(false);
    }
}
