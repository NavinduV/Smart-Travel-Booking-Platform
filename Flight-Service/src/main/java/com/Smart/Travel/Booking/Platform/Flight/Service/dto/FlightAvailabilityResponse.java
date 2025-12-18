package com.Smart.Travel.Booking.Platform.Flight.Service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightAvailabilityResponse {
    private Long flightId;
    private String flightNumber;
    private boolean available;
    private Integer availableSeats;
    private String message;
}
