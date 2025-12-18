package com.Smart.Travel.Booking.Platform.Flight.Service.dto;

import com.Smart.Travel.Booking.Platform.Flight.Service.entity.Flight.FlightStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDTO {
    private Long id;
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private Integer totalSeats;
    private Integer availableSeats;
    private FlightStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
