package com.Smart.Travel.Booking.Platform.Notification.Service.service;

import com.Smart.Travel.Booking.Platform.Notification.Service.dto.BulkNotificationRequest;
import com.Smart.Travel.Booking.Platform.Notification.Service.dto.CreateNotificationRequest;
import com.Smart.Travel.Booking.Platform.Notification.Service.dto.NotificationDTO;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationStatus;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationType;
import com.Smart.Travel.Booking.Platform.Notification.Service.exception.NotificationException;
import com.Smart.Travel.Booking.Platform.Notification.Service.exception.ResourceNotFoundException;
import com.Smart.Travel.Booking.Platform.Notification.Service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationDTO sendNotification(CreateNotificationRequest request) {
        log.info("Sending notification to user: {}", request.getUserId());

        NotificationType type;
        try {
            type = NotificationType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotificationException("Invalid notification type: " + request.getType());
        }

        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .userEmail(request.getUserEmail())
                .type(type)
                .subject(request.getSubject())
                .message(request.getMessage())
                .bookingReference(request.getBookingReference())
                .status(NotificationStatus.PENDING)
                .retryCount(0)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        // Send notification
        try {
            simulateSendNotification(savedNotification);
            
            savedNotification.setStatus(NotificationStatus.SENT);
            savedNotification.setSentAt(LocalDateTime.now());
            Notification sentNotification = notificationRepository.save(savedNotification);
            
            log.info("Notification sent successfully to user: {}", request.getUserId());
            return mapToDTO(sentNotification);

        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage());
            savedNotification.setStatus(NotificationStatus.FAILED);
            savedNotification.setFailureReason(e.getMessage());
            notificationRepository.save(savedNotification);
            
            return mapToDTO(savedNotification);
        }
    }

    public List<NotificationDTO> sendBulkNotifications(BulkNotificationRequest request) {
        log.info("Sending bulk notifications to {} users", request.getUserIds().size());
        
        List<NotificationDTO> sentNotifications = new ArrayList<>();
        
        for (Long userId : request.getUserIds()) {
            CreateNotificationRequest individualRequest = CreateNotificationRequest.builder()
                    .userId(userId)
                    .type(request.getType())
                    .subject(request.getSubject())
                    .message(request.getMessage())
                    .build();
            
            try {
                NotificationDTO sent = sendNotification(individualRequest);
                sentNotifications.add(sent);
            } catch (Exception e) {
                log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
            }
        }
        
        return sentNotifications;
    }

    public NotificationDTO retryNotification(Long notificationId) {
        log.info("Retrying notification: {}", notificationId);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));

        if (notification.getStatus() == NotificationStatus.SENT) {
            throw new NotificationException("Notification has already been sent");
        }

        if (notification.getRetryCount() >= 3) {
            throw new NotificationException("Maximum retry attempts reached");
        }

        notification.setStatus(NotificationStatus.RETRYING);
        notification.setRetryCount(notification.getRetryCount() + 1);
        notificationRepository.save(notification);

        try {
            simulateSendNotification(notification);
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setFailureReason(null);
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setFailureReason(e.getMessage());
        }

        Notification updatedNotification = notificationRepository.save(notification);
        return mapToDTO(updatedNotification);
    }

    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long id) {
        log.info("Fetching notification with id: {}", id);
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));
        return mapToDTO(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        log.info("Fetching all notifications");
        return notificationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByUserId(Long userId) {
        log.info("Fetching notifications for user: {}", userId);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByStatus(NotificationStatus status) {
        log.info("Fetching notifications with status: {}", status);
        return notificationRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByType(NotificationType type) {
        log.info("Fetching notifications with type: {}", type);
        return notificationRepository.findByType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getNotificationsByBookingReference(String bookingReference) {
        log.info("Fetching notifications for booking: {}", bookingReference);
        return notificationRepository.findByBookingReference(bookingReference).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> getFailedNotifications() {
        log.info("Fetching failed notifications for retry");
        return notificationRepository.findByStatusAndRetryCountLessThan(NotificationStatus.FAILED, 3).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private void simulateSendNotification(Notification notification) {
        log.info("Simulating notification send: Type={}, To={}, Subject={}", 
                notification.getType(), 
                notification.getUserEmail() != null ? notification.getUserEmail() : "User#" + notification.getUserId(),
                notification.getSubject());
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Notification simulation completed successfully");
    }

    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .userEmail(notification.getUserEmail())
                .type(notification.getType())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .bookingReference(notification.getBookingReference())
                .status(notification.getStatus())
                .failureReason(notification.getFailureReason())
                .retryCount(notification.getRetryCount())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
