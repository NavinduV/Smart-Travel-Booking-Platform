package com.Smart.Travel.Booking.Platform.Booking.Service.dto;

import com.Smart.Travel.Booking.Platform.Booking.Service.entity.Booking.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDTO {
    private Long id;
    private Long userId;
    private Long flightId;
    private Long hotelId;
    private Integer numberOfPassengers;
    private Integer numberOfRooms;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalAmount;
    private BigDecimal flightCost;
    private BigDecimal hotelCost;
    private BookingStatus status;
    private Long paymentId;
    private String bookingReference;
    private String specialRequests;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
