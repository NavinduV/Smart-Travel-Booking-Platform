package com.Smart.Travel.Booking.Platform.Booking.Service.client;

import com.Smart.Travel.Booking.Platform.Booking.Service.dto.FlightAvailabilityResponse;
import com.Smart.Travel.Booking.Platform.Booking.Service.dto.FlightDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "flight-service", url = "${services.flight.url}")
public interface FlightServiceClient {

    @GetMapping("/api/flights/{id}")
    FlightDTO getFlightById(@PathVariable("id") Long id);

    @GetMapping("/api/flights/{id}/availability")
    FlightAvailabilityResponse checkAvailability(
            @PathVariable("id") Long id,
            @RequestParam("requiredSeats") Integer requiredSeats);

    @PostMapping("/api/flights/{id}/book")
    FlightDTO bookSeats(
            @PathVariable("id") Long id,
            @RequestParam("numberOfSeats") Integer numberOfSeats);

    @PostMapping("/api/flights/{id}/release")
    FlightDTO releaseSeats(
            @PathVariable("id") Long id,
            @RequestParam("numberOfSeats") Integer numberOfSeats);
}
