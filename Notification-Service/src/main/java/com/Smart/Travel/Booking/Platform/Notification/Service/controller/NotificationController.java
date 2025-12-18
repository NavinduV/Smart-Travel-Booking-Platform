package com.Smart.Travel.Booking.Platform.Notification.Service.controller;

import com.Smart.Travel.Booking.Platform.Notification.Service.dto.BulkNotificationRequest;
import com.Smart.Travel.Booking.Platform.Notification.Service.dto.CreateNotificationRequest;
import com.Smart.Travel.Booking.Platform.Notification.Service.dto.NotificationDTO;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationStatus;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationType;
import com.Smart.Travel.Booking.Platform.Notification.Service.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Management", description = "APIs for managing notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Send notification", description = "Sends a new notification to a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notification sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<NotificationDTO> sendNotification(
            @Valid @RequestBody CreateNotificationRequest request) {
        NotificationDTO notification = notificationService.sendNotification(request);
        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

    @PostMapping("/bulk")
    @Operation(summary = "Send bulk notifications", description = "Sends notifications to multiple users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notifications sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<List<NotificationDTO>> sendBulkNotifications(
            @Valid @RequestBody BulkNotificationRequest request) {
        List<NotificationDTO> notifications = notificationService.sendBulkNotifications(request);
        return new ResponseEntity<>(notifications, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID", description = "Retrieves a notification by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification found"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationDTO> getNotificationById(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        NotificationDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping
    @Operation(summary = "Get all notifications", description = "Retrieves all notifications in the system")
    @ApiResponse(responseCode = "200", description = "List of notifications retrieved successfully")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications by user", description = "Retrieves all notifications for a specific user")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get notifications by status", description = "Retrieves all notifications with a specific status")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByStatus(
            @Parameter(description = "Notification status") @PathVariable NotificationStatus status) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get notifications by type", description = "Retrieves all notifications of a specific type")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByType(
            @Parameter(description = "Notification type") @PathVariable NotificationType type) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByType(type);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/booking/{bookingReference}")
    @Operation(summary = "Get notifications by booking reference", description = "Retrieves all notifications for a specific booking")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByBookingReference(
            @Parameter(description = "Booking reference") @PathVariable String bookingReference) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByBookingReference(bookingReference);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/failed")
    @Operation(summary = "Get failed notifications", description = "Retrieves all failed notifications that can be retried")
    @ApiResponse(responseCode = "200", description = "Failed notifications retrieved successfully")
    public ResponseEntity<List<NotificationDTO>> getFailedNotifications() {
        List<NotificationDTO> notifications = notificationService.getFailedNotifications();
        return ResponseEntity.ok(notifications);
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry notification", description = "Retries sending a failed notification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification retry attempted"),
            @ApiResponse(responseCode = "400", description = "Cannot retry notification"),
            @ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<NotificationDTO> retryNotification(
            @Parameter(description = "Notification ID") @PathVariable Long id) {
        NotificationDTO notification = notificationService.retryNotification(id);
        return ResponseEntity.ok(notification);
    }
}
