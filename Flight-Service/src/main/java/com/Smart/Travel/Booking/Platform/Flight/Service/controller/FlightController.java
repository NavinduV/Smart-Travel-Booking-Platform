package com.Smart.Travel.Booking.Platform.Flight.Service.controller;

import com.Smart.Travel.Booking.Platform.Flight.Service.dto.*;
import com.Smart.Travel.Booking.Platform.Flight.Service.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
@Tag(name = "Flight Management", description = "APIs for managing flights")
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    @Operation(summary = "Create a new flight", description = "Creates a new flight in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Flight with number already exists")
    })
    public ResponseEntity<FlightDTO> createFlight(@Valid @RequestBody CreateFlightRequest request) {
        FlightDTO createdFlight = flightService.createFlight(request);
        return new ResponseEntity<>(createdFlight, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get flight by ID", description = "Retrieves a flight by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight found"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightDTO> getFlightById(
            @Parameter(description = "Flight ID") @PathVariable Long id) {
        FlightDTO flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @GetMapping("/number/{flightNumber}")
    @Operation(summary = "Get flight by number", description = "Retrieves a flight by its flight number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight found"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightDTO> getFlightByNumber(
            @Parameter(description = "Flight number") @PathVariable String flightNumber) {
        FlightDTO flight = flightService.getFlightByNumber(flightNumber);
        return ResponseEntity.ok(flight);
    }

    @GetMapping
    @Operation(summary = "Get all flights", description = "Retrieves all flights in the system")
    @ApiResponse(responseCode = "200", description = "List of flights retrieved successfully")
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        List<FlightDTO> flights = flightService.getAllFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available flights", description = "Retrieves all flights with available seats")
    @ApiResponse(responseCode = "200", description = "List of available flights retrieved successfully")
    public ResponseEntity<List<FlightDTO>> getAvailableFlights() {
        List<FlightDTO> flights = flightService.getAvailableFlights();
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/search")
    @Operation(summary = "Search flights", description = "Search for available flights by route and date")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<FlightDTO>> searchFlights(
            @Parameter(description = "Origin city") @RequestParam String origin,
            @Parameter(description = "Destination city") @RequestParam String destination,
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Required seats") @RequestParam(defaultValue = "1") Integer requiredSeats) {
        List<FlightDTO> flights = flightService.searchFlights(origin, destination, startDate, endDate, requiredSeats);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/route")
    @Operation(summary = "Get flights by route", description = "Retrieves all flights for a specific route")
    @ApiResponse(responseCode = "200", description = "Flights retrieved successfully")
    public ResponseEntity<List<FlightDTO>> getFlightsByRoute(
            @Parameter(description = "Origin city") @RequestParam String origin,
            @Parameter(description = "Destination city") @RequestParam String destination) {
        List<FlightDTO> flights = flightService.getFlightsByRoute(origin, destination);
        return ResponseEntity.ok(flights);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update flight", description = "Updates an existing flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Flight not found"),
            @ApiResponse(responseCode = "409", description = "Flight number already in use")
    })
    public ResponseEntity<FlightDTO> updateFlight(
            @Parameter(description = "Flight ID") @PathVariable Long id,
            @Valid @RequestBody UpdateFlightRequest request) {
        FlightDTO updatedFlight = flightService.updateFlight(id, request);
        return ResponseEntity.ok(updatedFlight);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete flight", description = "Deletes a flight from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Flight deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<Void> deleteFlight(
            @Parameter(description = "Flight ID") @PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check flight availability", description = "Checks if seats are available on a flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability check completed"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightAvailabilityResponse> checkAvailability(
            @Parameter(description = "Flight ID") @PathVariable Long id,
            @Parameter(description = "Required seats") @RequestParam(defaultValue = "1") Integer requiredSeats) {
        FlightAvailabilityResponse response = flightService.checkAvailability(id, requiredSeats);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/book")
    @Operation(summary = "Book seats on flight", description = "Books a number of seats on a flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seats booked successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient seats available"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightDTO> bookSeats(
            @Parameter(description = "Flight ID") @PathVariable Long id,
            @Parameter(description = "Number of seats to book") @RequestParam Integer numberOfSeats) {
        FlightDTO flight = flightService.bookSeats(id, numberOfSeats);
        return ResponseEntity.ok(flight);
    }

    @PostMapping("/{id}/release")
    @Operation(summary = "Release seats on flight", description = "Releases previously booked seats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Seats released successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid number of seats"),
            @ApiResponse(responseCode = "404", description = "Flight not found")
    })
    public ResponseEntity<FlightDTO> releaseSeats(
            @Parameter(description = "Flight ID") @PathVariable Long id,
            @Parameter(description = "Number of seats to release") @RequestParam Integer numberOfSeats) {
        FlightDTO flight = flightService.releaseSeats(id, numberOfSeats);
        return ResponseEntity.ok(flight);
    }
}
