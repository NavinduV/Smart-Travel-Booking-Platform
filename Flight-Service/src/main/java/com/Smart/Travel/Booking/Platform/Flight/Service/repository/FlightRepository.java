package com.Smart.Travel.Booking.Platform.Flight.Service.repository;

import com.Smart.Travel.Booking.Platform.Flight.Service.entity.Flight;
import com.Smart.Travel.Booking.Platform.Flight.Service.entity.Flight.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumber(String flightNumber);

    boolean existsByFlightNumber(String flightNumber);

    List<Flight> findByOriginAndDestination(String origin, String destination);

    List<Flight> findByStatus(FlightStatus status);

    @Query("SELECT f FROM Flight f WHERE f.origin = :origin AND f.destination = :destination " +
           "AND f.departureTime >= :startDate AND f.departureTime <= :endDate " +
           "AND f.availableSeats >= :requiredSeats AND f.status = 'SCHEDULED'")
    List<Flight> searchAvailableFlights(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("requiredSeats") Integer requiredSeats);

    @Query("SELECT f FROM Flight f WHERE f.availableSeats > 0 AND f.status = 'SCHEDULED'")
    List<Flight> findAllAvailableFlights();

    List<Flight> findByAirline(String airline);
}
