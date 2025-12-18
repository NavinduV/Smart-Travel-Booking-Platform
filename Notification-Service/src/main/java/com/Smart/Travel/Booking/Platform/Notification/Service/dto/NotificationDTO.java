package com.Smart.Travel.Booking.Platform.Notification.Service.dto;

import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationStatus;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private NotificationType type;
    private String subject;
    private String message;
    private String bookingReference;
    private NotificationStatus status;
    private String failureReason;
    private Integer retryCount;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
