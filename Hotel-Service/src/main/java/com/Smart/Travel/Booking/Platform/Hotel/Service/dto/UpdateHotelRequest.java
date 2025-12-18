package com.Smart.Travel.Booking.Platform.Hotel.Service.dto;

import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateHotelRequest {

    private String name;
    private String city;
    private String address;
    private String description;
    private Integer starRating;

    @Positive(message = "Price must be positive")
    private BigDecimal pricePerNight;

    @Positive(message = "Total rooms must be positive")
    private Integer totalRooms;

    private String amenities;
    private String phoneNumber;
    private String email;
    private Boolean isActive;
}
