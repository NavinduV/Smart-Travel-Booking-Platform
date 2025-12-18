package com.Smart.Travel.Booking.Platform.Payment.Service.service;

import com.Smart.Travel.Booking.Platform.Payment.Service.dto.BookingDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.booking.url}")
    private String bookingServiceUrl;

    public Mono<BookingDTO> getBookingById(Long bookingId) {
        log.info("Fetching booking with id: {} from Booking Service", bookingId);
        return webClientBuilder.build()
                .get()
                .uri(bookingServiceUrl + "/api/bookings/{id}", bookingId)
                .retrieve()
                .bodyToMono(BookingDTO.class)
                .doOnSuccess(booking -> log.info("Successfully fetched booking: {}", booking.getBookingReference()))
                .doOnError(error -> log.error("Error fetching booking: {}", error.getMessage()));
    }

    public Mono<BookingDTO> updateBookingPaymentId(Long bookingId, Long paymentId) {
        log.info("Updating booking {} with payment id: {}", bookingId, paymentId);
        return webClientBuilder.build()
                .put()
                .uri(bookingServiceUrl + "/api/bookings/{id}/payment/{paymentId}", bookingId, paymentId)
                .retrieve()
                .bodyToMono(BookingDTO.class)
                .doOnSuccess(booking -> log.info("Successfully updated booking payment"))
                .doOnError(error -> log.error("Error updating booking payment: {}", error.getMessage()));
    }
}
