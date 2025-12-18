package com.Smart.Travel.Booking.Platform.Hotel.Service.controller;

import com.Smart.Travel.Booking.Platform.Hotel.Service.dto.*;
import com.Smart.Travel.Booking.Platform.Hotel.Service.service.HotelService;
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

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Tag(name = "Hotel Management", description = "APIs for managing hotels")
public class HotelController {

    private final HotelService hotelService;

    @PostMapping
    @Operation(summary = "Create a new hotel", description = "Creates a new hotel in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Hotel created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<HotelDTO> createHotel(@Valid @RequestBody CreateHotelRequest request) {
        HotelDTO createdHotel = hotelService.createHotel(request);
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get hotel by ID", description = "Retrieves a hotel by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel found"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelDTO> getHotelById(
            @Parameter(description = "Hotel ID") @PathVariable Long id) {
        HotelDTO hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping
    @Operation(summary = "Get all hotels", description = "Retrieves all hotels in the system")
    @ApiResponse(responseCode = "200", description = "List of hotels retrieved successfully")
    public ResponseEntity<List<HotelDTO>> getAllHotels() {
        List<HotelDTO> hotels = hotelService.getAllHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active hotels", description = "Retrieves all active hotels")
    @ApiResponse(responseCode = "200", description = "List of active hotels retrieved successfully")
    public ResponseEntity<List<HotelDTO>> getActiveHotels() {
        List<HotelDTO> hotels = hotelService.getActiveHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available hotels", description = "Retrieves all hotels with available rooms")
    @ApiResponse(responseCode = "200", description = "List of available hotels retrieved successfully")
    public ResponseEntity<List<HotelDTO>> getAvailableHotels() {
        List<HotelDTO> hotels = hotelService.getAvailableHotels();
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Get hotels by city", description = "Retrieves all hotels in a specific city")
    @ApiResponse(responseCode = "200", description = "Hotels retrieved successfully")
    public ResponseEntity<List<HotelDTO>> getHotelsByCity(
            @Parameter(description = "City name") @PathVariable String city) {
        List<HotelDTO> hotels = hotelService.getHotelsByCity(city);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/search")
    @Operation(summary = "Search hotels", description = "Search for available hotels by criteria")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    public ResponseEntity<List<HotelDTO>> searchHotels(
            @Parameter(description = "City name") @RequestParam String city,
            @Parameter(description = "Maximum price per night") @RequestParam BigDecimal maxPrice,
            @Parameter(description = "Required rooms") @RequestParam(defaultValue = "1") Integer requiredRooms) {
        List<HotelDTO> hotels = hotelService.searchHotels(city, maxPrice, requiredRooms);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/city/{city}/available")
    @Operation(summary = "Get available hotels by city", description = "Retrieves available hotels in a city")
    @ApiResponse(responseCode = "200", description = "Available hotels retrieved successfully")
    public ResponseEntity<List<HotelDTO>> getAvailableHotelsByCity(
            @Parameter(description = "City name") @PathVariable String city,
            @Parameter(description = "Required rooms") @RequestParam(defaultValue = "1") Integer requiredRooms) {
        List<HotelDTO> hotels = hotelService.getAvailableHotelsByCity(city, requiredRooms);
        return ResponseEntity.ok(hotels);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update hotel", description = "Updates an existing hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelDTO> updateHotel(
            @Parameter(description = "Hotel ID") @PathVariable Long id,
            @Valid @RequestBody UpdateHotelRequest request) {
        HotelDTO updatedHotel = hotelService.updateHotel(id, request);
        return ResponseEntity.ok(updatedHotel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete hotel (soft delete)", description = "Soft deletes a hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hotel deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<Void> deleteHotel(
            @Parameter(description = "Hotel ID") @PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "Hard delete hotel", description = "Permanently deletes a hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Hotel permanently deleted"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<Void> hardDeleteHotel(
            @Parameter(description = "Hotel ID") @PathVariable Long id) {
        hotelService.hardDeleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Check hotel availability", description = "Checks if rooms are available at a hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Availability check completed"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelAvailabilityResponse> checkAvailability(
            @Parameter(description = "Hotel ID") @PathVariable Long id,
            @Parameter(description = "Required rooms") @RequestParam(defaultValue = "1") Integer requiredRooms) {
        HotelAvailabilityResponse response = hotelService.checkAvailability(id, requiredRooms);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/book")
    @Operation(summary = "Book rooms at hotel", description = "Books rooms at a hotel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms booked successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient rooms available"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelDTO> bookRooms(
            @Parameter(description = "Hotel ID") @PathVariable Long id,
            @Parameter(description = "Number of rooms to book") @RequestParam Integer numberOfRooms) {
        HotelDTO hotel = hotelService.bookRooms(id, numberOfRooms);
        return ResponseEntity.ok(hotel);
    }

    @PostMapping("/{id}/release")
    @Operation(summary = "Release rooms at hotel", description = "Releases previously booked rooms")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rooms released successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid number of rooms"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    public ResponseEntity<HotelDTO> releaseRooms(
            @Parameter(description = "Hotel ID") @PathVariable Long id,
            @Parameter(description = "Number of rooms to release") @RequestParam Integer numberOfRooms) {
        HotelDTO hotel = hotelService.releaseRooms(id, numberOfRooms);
        return ResponseEntity.ok(hotel);
    }
}
