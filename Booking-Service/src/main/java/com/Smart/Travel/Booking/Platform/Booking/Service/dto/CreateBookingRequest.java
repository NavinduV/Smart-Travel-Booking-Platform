package com.Smart.Travel.Booking.Platform.Booking.Service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private Long flightId;

    private Long hotelId;

    @Positive(message = "Number of passengers must be positive")
    private Integer numberOfPassengers;

    @Positive(message = "Number of rooms must be positive")
    private Integer numberOfRooms;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private String specialRequests;
}
