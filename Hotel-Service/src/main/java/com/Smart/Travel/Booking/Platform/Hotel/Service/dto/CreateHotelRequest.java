package com.Smart.Travel.Booking.Platform.Hotel.Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateHotelRequest {

    @NotBlank(message = "Hotel name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address is required")
    private String address;

    private String description;

    @NotNull(message = "Star rating is required")
    private Integer starRating;

    @NotNull(message = "Price per night is required")
    @Positive(message = "Price must be positive")
    private BigDecimal pricePerNight;

    @NotNull(message = "Total rooms is required")
    @Positive(message = "Total rooms must be positive")
    private Integer totalRooms;

    private String amenities;

    private String phoneNumber;

    private String email;
}
