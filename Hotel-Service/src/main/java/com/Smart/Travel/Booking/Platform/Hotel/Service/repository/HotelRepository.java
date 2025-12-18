package com.Smart.Travel.Booking.Platform.Hotel.Service.repository;

import com.Smart.Travel.Booking.Platform.Hotel.Service.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCity(String city);

    List<Hotel> findByCityAndIsActiveTrue(String city);

    List<Hotel> findByIsActiveTrue();

    Optional<Hotel> findByIdAndIsActiveTrue(Long id);

    List<Hotel> findByStarRatingGreaterThanEqual(Integer starRating);

    @Query("SELECT h FROM Hotel h WHERE h.city = :city AND h.availableRooms >= :requiredRooms " +
           "AND h.isActive = true")
    List<Hotel> findAvailableHotelsByCity(
            @Param("city") String city,
            @Param("requiredRooms") Integer requiredRooms);

    @Query("SELECT h FROM Hotel h WHERE h.city = :city AND h.pricePerNight <= :maxPrice " +
           "AND h.availableRooms >= :requiredRooms AND h.isActive = true")
    List<Hotel> searchHotels(
            @Param("city") String city,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("requiredRooms") Integer requiredRooms);

    @Query("SELECT h FROM Hotel h WHERE h.availableRooms > 0 AND h.isActive = true")
    List<Hotel> findAllAvailableHotels();

    List<Hotel> findByNameContainingIgnoreCase(String name);
}
