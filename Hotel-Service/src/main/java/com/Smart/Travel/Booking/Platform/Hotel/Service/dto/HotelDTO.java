package com.Smart.Travel.Booking.Platform.Hotel.Service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private String phoneNumber;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
