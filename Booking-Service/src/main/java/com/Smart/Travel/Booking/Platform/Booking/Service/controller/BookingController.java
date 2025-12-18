package com.Smart.Travel.Booking.Platform.Booking.Service.controller;

import com.Smart.Travel.Booking.Platform.Booking.Service.dto.BookingDTO;
import com.Smart.Travel.Booking.Platform.Booking.Service.dto.CreateBookingRequest;
import com.Smart.Travel.Booking.Platform.Booking.Service.entity.Booking.BookingStatus;
import com.Smart.Travel.Booking.Platform.Booking.Service.service.BookingService;
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
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Booking Management", description = "APIs for managing travel bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking", description = "Creates a new travel booking for flights and/or hotels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or booking failed"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<BookingDTO> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        BookingDTO createdBooking = bookingService.createBooking(request);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID", description = "Retrieves a booking by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDTO> getBookingById(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        BookingDTO booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/reference/{reference}")
    @Operation(summary = "Get booking by reference", description = "Retrieves a booking by its reference number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking found"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDTO> getBookingByReference(
            @Parameter(description = "Booking reference") @PathVariable String reference) {
        BookingDTO booking = bookingService.getBookingByReference(reference);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    @Operation(summary = "Get all bookings", description = "Retrieves all bookings in the system")
    @ApiResponse(responseCode = "200", description = "List of bookings retrieved successfully")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get bookings by user", description = "Retrieves all bookings for a specific user")
    @ApiResponse(responseCode = "200", description = "User bookings retrieved successfully")
    public ResponseEntity<List<BookingDTO>> getBookingsByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<BookingDTO> bookings = bookingService.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get bookings by status", description = "Retrieves all bookings with a specific status")
    @ApiResponse(responseCode = "200", description = "Bookings retrieved successfully")
    public ResponseEntity<List<BookingDTO>> getBookingsByStatus(
            @Parameter(description = "Booking status") @PathVariable BookingStatus status) {
        List<BookingDTO> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm booking", description = "Confirms a pending booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking confirmed successfully"),
            @ApiResponse(responseCode = "400", description = "Booking cannot be confirmed"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDTO> confirmBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        BookingDTO confirmedBooking = bookingService.confirmBooking(id);
        return ResponseEntity.ok(confirmedBooking);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancels an existing booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Booking cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDTO> cancelBooking(
            @Parameter(description = "Booking ID") @PathVariable Long id) {
        BookingDTO cancelledBooking = bookingService.cancelBooking(id);
        return ResponseEntity.ok(cancelledBooking);
    }

    @PutMapping("/{id}/payment/{paymentId}")
    @Operation(summary = "Update payment ID", description = "Updates the payment ID for a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment ID updated successfully"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    public ResponseEntity<BookingDTO> updatePaymentId(
            @Parameter(description = "Booking ID") @PathVariable Long id,
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        BookingDTO updatedBooking = bookingService.updatePaymentId(id, paymentId);
        return ResponseEntity.ok(updatedBooking);
    }
}
