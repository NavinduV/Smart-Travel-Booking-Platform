package com.Smart.Travel.Booking.Platform.Booking.Service.service;

import com.Smart.Travel.Booking.Platform.Booking.Service.dto.NotificationRequest;
import com.Smart.Travel.Booking.Platform.Booking.Service.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.notification.url}")
    private String notificationServiceUrl;

    public Mono<NotificationResponse> sendNotification(NotificationRequest request) {
        log.info("Sending notification to user: {}", request.getUserId());
        return webClientBuilder.build()
                .post()
                .uri(notificationServiceUrl + "/api/notifications")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(NotificationResponse.class)
                .doOnSuccess(response -> log.info("Notification sent successfully: {}", response.getId()))
                .doOnError(error -> log.error("Error sending notification: {}", error.getMessage()));
    }

    public Mono<NotificationResponse> sendBookingConfirmation(Long userId, String userEmail, 
                                                               String bookingReference, String message) {
        NotificationRequest request = NotificationRequest.builder()
                .userId(userId)
                .userEmail(userEmail)
                .type("BOOKING_CONFIRMATION")
                .subject("Booking Confirmed - " + bookingReference)
                .message(message)
                .bookingReference(bookingReference)
                .build();
        
        return sendNotification(request);
    }

    public Mono<NotificationResponse> sendBookingCancellation(Long userId, String userEmail,
                                                               String bookingReference, String message) {
        NotificationRequest request = NotificationRequest.builder()
                .userId(userId)
                .userEmail(userEmail)
                .type("BOOKING_CANCELLATION")
                .subject("Booking Cancelled - " + bookingReference)
                .message(message)
                .bookingReference(bookingReference)
                .build();
        
        return sendNotification(request);
    }
}
