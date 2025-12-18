package com.Smart.Travel.Booking.Platform.Flight.Service.dto;

import com.Smart.Travel.Booking.Platform.Flight.Service.entity.Flight.FlightStatus;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFlightRequest {

    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Positive(message = "Total seats must be positive")
    private Integer totalSeats;

    private FlightStatus status;
}
