package com.Smart.Travel.Booking.Platform.Notification.Service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkNotificationRequest {

    @NotNull(message = "User IDs are required")
    private List<Long> userIds;

    @NotNull(message = "Notification type is required")
    private String type;

    @NotNull(message = "Subject is required")
    private String subject;

    @NotNull(message = "Message is required")
    private String message;
}
