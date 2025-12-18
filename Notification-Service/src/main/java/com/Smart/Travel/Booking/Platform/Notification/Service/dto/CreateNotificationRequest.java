package com.Smart.Travel.Booking.Platform.Notification.Service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private String userEmail;

    @NotNull(message = "Notification type is required")
    private String type;

    @NotNull(message = "Subject is required")
    private String subject;

    @NotNull(message = "Message is required")
    private String message;

    private String bookingReference;
}
