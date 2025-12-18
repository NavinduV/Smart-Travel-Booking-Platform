package com.Smart.Travel.Booking.Platform.Booking.Service.repository;

import com.Smart.Travel.Booking.Platform.Booking.Service.entity.Booking;
import com.Smart.Travel.Booking.Platform.Booking.Service.entity.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByStatus(BookingStatus status);

    List<Booking> findByUserIdAndStatus(Long userId, BookingStatus status);

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByFlightId(Long flightId);

    List<Booking> findByHotelId(Long hotelId);

    List<Booking> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
}
