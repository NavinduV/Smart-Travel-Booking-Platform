package com.Smart.Travel.Booking.Platform.Booking.Service.client;

import com.Smart.Travel.Booking.Platform.Booking.Service.dto.HotelAvailabilityResponse;
import com.Smart.Travel.Booking.Platform.Booking.Service.dto.HotelDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "hotel-service", url = "${services.hotel.url}")
public interface HotelServiceClient {

    @GetMapping("/api/hotels/{id}")
    HotelDTO getHotelById(@PathVariable("id") Long id);

    @GetMapping("/api/hotels/{id}/availability")
    HotelAvailabilityResponse checkAvailability(
            @PathVariable("id") Long id,
            @RequestParam("requiredRooms") Integer requiredRooms);

    @PostMapping("/api/hotels/{id}/book")
    HotelDTO bookRooms(
            @PathVariable("id") Long id,
            @RequestParam("numberOfRooms") Integer numberOfRooms);

    @PostMapping("/api/hotels/{id}/release")
    HotelDTO releaseRooms(
            @PathVariable("id") Long id,
            @RequestParam("numberOfRooms") Integer numberOfRooms);
}
