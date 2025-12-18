package com.Smart.Travel.Booking.Platform.Notification.Service.repository;

import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationStatus;
import com.Smart.Travel.Booking.Platform.Notification.Service.entity.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    List<Notification> findByUserIdAndStatus(Long userId, NotificationStatus status);

    List<Notification> findByBookingReference(String bookingReference);

    List<Notification> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByStatusAndRetryCountLessThan(NotificationStatus status, Integer maxRetries);
}
