package com.Smart.Travel.Booking.Platform.Hotel.Service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelAvailabilityResponse {
    private Long hotelId;
    private String hotelName;
    private boolean available;
    private Integer availableRooms;
    private String message;
}
