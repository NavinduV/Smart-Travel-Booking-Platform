package com.Smart.Travel.Booking.Platform.Booking.Service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {
    private Long userId;
    private String userEmail;
    private String type;
    private String subject;
    private String message;
    private String bookingReference;
}
