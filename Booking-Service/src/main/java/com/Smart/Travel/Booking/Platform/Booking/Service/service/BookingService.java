package com.Smart.Travel.Booking.Platform.Booking.Service.service;

import com.Smart.Travel.Booking.Platform.Booking.Service.client.FlightServiceClient;
import com.Smart.Travel.Booking.Platform.Booking.Service.client.HotelServiceClient;
import com.Smart.Travel.Booking.Platform.Booking.Service.dto.*;
import com.Smart.Travel.Booking.Platform.Booking.Service.entity.Booking;
import com.Smart.Travel.Booking.Platform.Booking.Service.entity.Booking.BookingStatus;
import com.Smart.Travel.Booking.Platform.Booking.Service.exception.BookingException;
import com.Smart.Travel.Booking.Platform.Booking.Service.exception.ResourceNotFoundException;
import com.Smart.Travel.Booking.Platform.Booking.Service.exception.ServiceUnavailableException;
import com.Smart.Travel.Booking.Platform.Booking.Service.repository.BookingRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightServiceClient flightServiceClient;
    private final HotelServiceClient hotelServiceClient;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public BookingDTO createBooking(CreateBookingRequest request) {
        log.info("Creating booking for user: {}", request.getUserId());

        // Validate user using WebClient
        Boolean isUserValid = userServiceClient.validateUser(request.getUserId()).block();
        if (isUserValid == null || !isUserValid) {
            throw new BookingException("User validation failed. User ID: " + request.getUserId() + " is not valid or inactive");
        }

        UserDTO user = userServiceClient.getUserById(request.getUserId()).block();
        if (user == null) {
            throw new BookingException("Unable to fetch user details");
        }

        BigDecimal flightCost = BigDecimal.ZERO;
        BigDecimal hotelCost = BigDecimal.ZERO;
        FlightDTO flight = null;
        HotelDTO hotel = null;

        // Flight availability check using Feign Client
        if (request.getFlightId() != null) {
            try {
                FlightAvailabilityResponse flightAvailability = flightServiceClient.checkAvailability(
                        request.getFlightId(),
                        request.getNumberOfPassengers() != null ? request.getNumberOfPassengers() : 1
                );

                if (!flightAvailability.isAvailable()) {
                    throw new BookingException("Flight not available: " + flightAvailability.getMessage());
                }

                flight = flightServiceClient.getFlightById(request.getFlightId());
                flightCost = flight.getPrice().multiply(BigDecimal.valueOf(
                        request.getNumberOfPassengers() != null ? request.getNumberOfPassengers() : 1
                ));
            } catch (FeignException e) {
                log.error("Error communicating with Flight Service: {}", e.getMessage());
                throw new ServiceUnavailableException("Flight Service is unavailable");
            }
        }

        // hotel availability check using Feign Client
        if (request.getHotelId() != null) {
            try {
                HotelAvailabilityResponse hotelAvailability = hotelServiceClient.checkAvailability(
                        request.getHotelId(),
                        request.getNumberOfRooms() != null ? request.getNumberOfRooms() : 1
                );

                if (!hotelAvailability.isAvailable()) {
                    throw new BookingException("Hotel not available: " + hotelAvailability.getMessage());
                }

                hotel = hotelServiceClient.getHotelById(request.getHotelId());
                
                // Calculate number of nights
                long numberOfNights = 1;
                if (request.getCheckInDate() != null && request.getCheckOutDate() != null) {
                    numberOfNights = ChronoUnit.DAYS.between(
                            request.getCheckInDate(),
                            request.getCheckOutDate()
                    );
                    if (numberOfNights < 1) numberOfNights = 1;
                }

                hotelCost = hotel.getPricePerNight()
                        .multiply(BigDecimal.valueOf(numberOfNights))
                        .multiply(BigDecimal.valueOf(request.getNumberOfRooms() != null ? request.getNumberOfRooms() : 1));
            } catch (FeignException e) {
                log.error("Error communicating with Hotel Service: {}", e.getMessage());
                throw new ServiceUnavailableException("Hotel Service is unavailable");
            }
        }

        // Calculate total cost
        BigDecimal totalAmount = flightCost.add(hotelCost);

        // Save booking
        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .flightId(request.getFlightId())
                .hotelId(request.getHotelId())
                .numberOfPassengers(request.getNumberOfPassengers())
                .numberOfRooms(request.getNumberOfRooms())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .flightCost(flightCost)
                .hotelCost(hotelCost)
                .totalAmount(totalAmount)
                .specialRequests(request.getSpecialRequests())
                .status(BookingStatus.PENDING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created with reference: {}", savedBooking.getBookingReference());

        // Book the flight and hotel
        try {
            if (request.getFlightId() != null) {
                flightServiceClient.bookSeats(request.getFlightId(), 
                        request.getNumberOfPassengers() != null ? request.getNumberOfPassengers() : 1);
            }
            if (request.getHotelId() != null) {
                hotelServiceClient.bookRooms(request.getHotelId(), 
                        request.getNumberOfRooms() != null ? request.getNumberOfRooms() : 1);
            }
        } catch (FeignException e) {
            log.error("Error booking resources: {}", e.getMessage());
            savedBooking.setStatus(BookingStatus.FAILED);
            bookingRepository.save(savedBooking);
            throw new BookingException("Failed to book resources: " + e.getMessage());
        }

        // Send notification
        try {
            String message = String.format(
                    "Dear %s %s, your booking %s has been created successfully. Total amount: $%s",
                    user.getFirstName(), user.getLastName(),
                    savedBooking.getBookingReference(),
                    savedBooking.getTotalAmount()
            );
            notificationServiceClient.sendBookingConfirmation(
                    user.getId(),
                    user.getEmail(),
                    savedBooking.getBookingReference(),
                    message
            ).subscribe();
        } catch (Exception e) {
            log.warn("Failed to send notification: {}", e.getMessage());
        }

        return mapToDTO(savedBooking);
    }

    public BookingDTO confirmBooking(Long bookingId) {
        log.info("Confirming booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingException("Only pending bookings can be confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking confirmedBooking = bookingRepository.save(booking);

        try {
            UserDTO user = userServiceClient.getUserById(booking.getUserId()).block();
            if (user != null) {
                String message = String.format(
                        "Dear %s, your booking %s has been confirmed!",
                        user.getFirstName(),
                        booking.getBookingReference()
                );
                notificationServiceClient.sendBookingConfirmation(
                        user.getId(),
                        user.getEmail(),
                        booking.getBookingReference(),
                        message
                ).subscribe();
            }
        } catch (Exception e) {
            log.warn("Failed to send confirmation notification: {}", e.getMessage());
        }

        return mapToDTO(confirmedBooking);
    }

    public BookingDTO cancelBooking(Long bookingId) {
        log.info("Cancelling booking: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }

        if (booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BookingException("Cannot cancel a completed booking");
        }

        try {
            if (booking.getFlightId() != null) {
                flightServiceClient.releaseSeats(booking.getFlightId(), 
                        booking.getNumberOfPassengers() != null ? booking.getNumberOfPassengers() : 1);
            }
            if (booking.getHotelId() != null) {
                hotelServiceClient.releaseRooms(booking.getHotelId(), 
                        booking.getNumberOfRooms() != null ? booking.getNumberOfRooms() : 1);
            }
        } catch (FeignException e) {
            log.error("Error releasing resources: {}", e.getMessage());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking cancelledBooking = bookingRepository.save(booking);

        try {
            UserDTO user = userServiceClient.getUserById(booking.getUserId()).block();
            if (user != null) {
                String message = String.format(
                        "Dear %s, your booking %s has been cancelled.",
                        user.getFirstName(),
                        booking.getBookingReference()
                );
                notificationServiceClient.sendBookingCancellation(
                        user.getId(),
                        user.getEmail(),
                        booking.getBookingReference(),
                        message
                ).subscribe();
            }
        } catch (Exception e) {
            log.warn("Failed to send cancellation notification: {}", e.getMessage());
        }

        return mapToDTO(cancelledBooking);
    }

    @Transactional(readOnly = true)
    public BookingDTO getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
        return mapToDTO(booking);
    }

    @Transactional(readOnly = true)
    public BookingDTO getBookingByReference(String reference) {
        log.info("Fetching booking with reference: {}", reference);
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + reference));
        return mapToDTO(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByStatus(BookingStatus status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BookingDTO updatePaymentId(Long bookingId, Long paymentId) {
        log.info("Updating payment ID for booking: {}", bookingId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        
        booking.setPaymentId(paymentId);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        
        return mapToDTO(updatedBooking);
    }

    private BookingDTO mapToDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .userId(booking.getUserId())
                .flightId(booking.getFlightId())
                .hotelId(booking.getHotelId())
                .numberOfPassengers(booking.getNumberOfPassengers())
                .numberOfRooms(booking.getNumberOfRooms())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .totalAmount(booking.getTotalAmount())
                .flightCost(booking.getFlightCost())
                .hotelCost(booking.getHotelCost())
                .status(booking.getStatus())
                .paymentId(booking.getPaymentId())
                .bookingReference(booking.getBookingReference())
                .specialRequests(booking.getSpecialRequests())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .build();
    }
}
