package com.Smart.Travel.Booking.Platform.Flight.Service.service;

import com.Smart.Travel.Booking.Platform.Flight.Service.dto.*;
import com.Smart.Travel.Booking.Platform.Flight.Service.entity.Flight;
import com.Smart.Travel.Booking.Platform.Flight.Service.entity.Flight.FlightStatus;
import com.Smart.Travel.Booking.Platform.Flight.Service.exception.DuplicateResourceException;
import com.Smart.Travel.Booking.Platform.Flight.Service.exception.InsufficientSeatsException;
import com.Smart.Travel.Booking.Platform.Flight.Service.exception.ResourceNotFoundException;
import com.Smart.Travel.Booking.Platform.Flight.Service.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightDTO createFlight(CreateFlightRequest request) {
        log.info("Creating new flight with number: {}", request.getFlightNumber());

        if (flightRepository.existsByFlightNumber(request.getFlightNumber())) {
            throw new DuplicateResourceException("Flight with number " + request.getFlightNumber() + " already exists");
        }

        Flight flight = Flight.builder()
                .flightNumber(request.getFlightNumber())
                .airline(request.getAirline())
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .price(request.getPrice())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .status(FlightStatus.SCHEDULED)
                .build();

        Flight savedFlight = flightRepository.save(flight);
        log.info("Flight created successfully with id: {}", savedFlight.getId());

        return mapToDTO(savedFlight);
    }

    @Transactional(readOnly = true)
    public FlightDTO getFlightById(Long id) {
        log.info("Fetching flight with id: {}", id);
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        return mapToDTO(flight);
    }

    @Transactional(readOnly = true)
    public FlightDTO getFlightByNumber(String flightNumber) {
        log.info("Fetching flight with number: {}", flightNumber);
        Flight flight = flightRepository.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with number: " + flightNumber));
        return mapToDTO(flight);
    }

    @Transactional(readOnly = true)
    public List<FlightDTO> getAllFlights() {
        log.info("Fetching all flights");
        return flightRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlightDTO> getAvailableFlights() {
        log.info("Fetching all available flights");
        return flightRepository.findAllAvailableFlights().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlightDTO> searchFlights(String origin, String destination, 
                                          LocalDateTime startDate, LocalDateTime endDate, 
                                          Integer requiredSeats) {
        log.info("Searching flights from {} to {} between {} and {}", origin, destination, startDate, endDate);
        return flightRepository.searchAvailableFlights(origin, destination, startDate, endDate, requiredSeats)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FlightDTO> getFlightsByRoute(String origin, String destination) {
        log.info("Fetching flights from {} to {}", origin, destination);
        return flightRepository.findByOriginAndDestination(origin, destination).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FlightDTO updateFlight(Long id, UpdateFlightRequest request) {
        log.info("Updating flight with id: {}", id);

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        if (request.getFlightNumber() != null && !request.getFlightNumber().equals(flight.getFlightNumber())) {
            if (flightRepository.existsByFlightNumber(request.getFlightNumber())) {
                throw new DuplicateResourceException("Flight with number " + request.getFlightNumber() + " already exists");
            }
            flight.setFlightNumber(request.getFlightNumber());
        }
        if (request.getAirline() != null) {
            flight.setAirline(request.getAirline());
        }
        if (request.getOrigin() != null) {
            flight.setOrigin(request.getOrigin());
        }
        if (request.getDestination() != null) {
            flight.setDestination(request.getDestination());
        }
        if (request.getDepartureTime() != null) {
            flight.setDepartureTime(request.getDepartureTime());
        }
        if (request.getArrivalTime() != null) {
            flight.setArrivalTime(request.getArrivalTime());
        }
        if (request.getPrice() != null) {
            flight.setPrice(request.getPrice());
        }
        if (request.getTotalSeats() != null) {
            int bookedSeats = flight.getTotalSeats() - flight.getAvailableSeats();
            if (request.getTotalSeats() < bookedSeats) {
                throw new IllegalArgumentException("Cannot reduce total seats below already booked seats");
            }
            flight.setAvailableSeats(request.getTotalSeats() - bookedSeats);
            flight.setTotalSeats(request.getTotalSeats());
        }
        if (request.getStatus() != null) {
            flight.setStatus(request.getStatus());
        }

        Flight updatedFlight = flightRepository.save(flight);
        log.info("Flight updated successfully with id: {}", updatedFlight.getId());

        return mapToDTO(updatedFlight);
    }

    public void deleteFlight(Long id) {
        log.info("Deleting flight with id: {}", id);

        if (!flightRepository.existsById(id)) {
            throw new ResourceNotFoundException("Flight not found with id: " + id);
        }

        flightRepository.deleteById(id);
        log.info("Flight deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public FlightAvailabilityResponse checkAvailability(Long id, Integer requiredSeats) {
        log.info("Checking availability for flight {} with {} seats", id, requiredSeats);

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        boolean isAvailable = flight.getStatus() == FlightStatus.SCHEDULED 
                && flight.getAvailableSeats() >= requiredSeats;

        String message = isAvailable 
                ? "Flight is available with " + flight.getAvailableSeats() + " seats"
                : flight.getStatus() != FlightStatus.SCHEDULED 
                    ? "Flight is " + flight.getStatus().toString().toLowerCase()
                    : "Only " + flight.getAvailableSeats() + " seats available";

        return FlightAvailabilityResponse.builder()
                .flightId(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .available(isAvailable)
                .availableSeats(flight.getAvailableSeats())
                .message(message)
                .build();
    }

    public FlightDTO bookSeats(Long id, Integer numberOfSeats) {
        log.info("Booking {} seats for flight {}", numberOfSeats, id);

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        if (flight.getStatus() != FlightStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot book seats on a " + flight.getStatus().toString().toLowerCase() + " flight");
        }

        if (flight.getAvailableSeats() < numberOfSeats) {
            throw new InsufficientSeatsException("Only " + flight.getAvailableSeats() + " seats available, requested: " + numberOfSeats);
        }

        flight.setAvailableSeats(flight.getAvailableSeats() - numberOfSeats);
        Flight updatedFlight = flightRepository.save(flight);
        
        log.info("Successfully booked {} seats for flight {}", numberOfSeats, id);
        return mapToDTO(updatedFlight);
    }

    public FlightDTO releaseSeats(Long id, Integer numberOfSeats) {
        log.info("Releasing {} seats for flight {}", numberOfSeats, id);

        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        int newAvailableSeats = flight.getAvailableSeats() + numberOfSeats;
        if (newAvailableSeats > flight.getTotalSeats()) {
            throw new IllegalArgumentException("Cannot release more seats than total capacity");
        }

        flight.setAvailableSeats(newAvailableSeats);
        Flight updatedFlight = flightRepository.save(flight);
        
        log.info("Successfully released {} seats for flight {}", numberOfSeats, id);
        return mapToDTO(updatedFlight);
    }

    private FlightDTO mapToDTO(Flight flight) {
        return FlightDTO.builder()
                .id(flight.getId())
                .flightNumber(flight.getFlightNumber())
                .airline(flight.getAirline())
                .origin(flight.getOrigin())
                .destination(flight.getDestination())
                .departureTime(flight.getDepartureTime())
                .arrivalTime(flight.getArrivalTime())
                .price(flight.getPrice())
                .totalSeats(flight.getTotalSeats())
                .availableSeats(flight.getAvailableSeats())
                .status(flight.getStatus())
                .createdAt(flight.getCreatedAt())
                .updatedAt(flight.getUpdatedAt())
                .build();
    }
}
