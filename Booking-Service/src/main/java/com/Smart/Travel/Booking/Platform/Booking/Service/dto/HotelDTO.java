package com.Smart.Travel.Booking.Platform.Booking.Service.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotelDTO {
    private Long id;
    private String name;
    private String city;
    private String address;
    private String description;
    private Integer starRating;
    private BigDecimal pricePerNight;
    private Integer totalRooms;
    private Integer availableRooms;
    private String amenities;
    private Boolean isActive;
}
