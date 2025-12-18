package com.Smart.Travel.Booking.Platform.Booking.Service.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String type;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime sentAt;
}
