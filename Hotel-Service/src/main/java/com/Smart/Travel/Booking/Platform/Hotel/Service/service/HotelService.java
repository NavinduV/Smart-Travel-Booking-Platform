package com.Smart.Travel.Booking.Platform.Hotel.Service.service;

import com.Smart.Travel.Booking.Platform.Hotel.Service.dto.*;
import com.Smart.Travel.Booking.Platform.Hotel.Service.entity.Hotel;
import com.Smart.Travel.Booking.Platform.Hotel.Service.exception.InsufficientRoomsException;
import com.Smart.Travel.Booking.Platform.Hotel.Service.exception.ResourceNotFoundException;
import com.Smart.Travel.Booking.Platform.Hotel.Service.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelDTO createHotel(CreateHotelRequest request) {
        log.info("Creating new hotel: {}", request.getName());

        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .city(request.getCity())
                .address(request.getAddress())
                .description(request.getDescription())
                .starRating(request.getStarRating())
                .pricePerNight(request.getPricePerNight())
                .totalRooms(request.getTotalRooms())
                .availableRooms(request.getTotalRooms())
                .amenities(request.getAmenities())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .isActive(true)
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created successfully with id: {}", savedHotel.getId());

        return mapToDTO(savedHotel);
    }

    @Transactional(readOnly = true)
    public HotelDTO getHotelById(Long id) {
        log.info("Fetching hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return mapToDTO(hotel);
    }

    @Transactional(readOnly = true)
    public List<HotelDTO> getAllHotels() {
        log.info("Fetching all hotels");
        return hotelRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDTO> getActiveHotels() {
        log.info("Fetching all active hotels");
        return hotelRepository.findByIsActiveTrue().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDTO> getAvailableHotels() {
        log.info("Fetching all available hotels");
        return hotelRepository.findAllAvailableHotels().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDTO> getHotelsByCity(String city) {
        log.info("Fetching hotels in city: {}", city);
        return hotelRepository.findByCityAndIsActiveTrue(city).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDTO> searchHotels(String city, BigDecimal maxPrice, Integer requiredRooms) {
        log.info("Searching hotels in {} with max price {} and {} rooms", city, maxPrice, requiredRooms);
        return hotelRepository.searchHotels(city, maxPrice, requiredRooms).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HotelDTO> getAvailableHotelsByCity(String city, Integer requiredRooms) {
        log.info("Fetching available hotels in city: {} with {} rooms", city, requiredRooms);
        return hotelRepository.findAvailableHotelsByCity(city, requiredRooms).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public HotelDTO updateHotel(Long id, UpdateHotelRequest request) {
        log.info("Updating hotel with id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        if (request.getName() != null) {
            hotel.setName(request.getName());
        }
        if (request.getCity() != null) {
            hotel.setCity(request.getCity());
        }
        if (request.getAddress() != null) {
            hotel.setAddress(request.getAddress());
        }
        if (request.getDescription() != null) {
            hotel.setDescription(request.getDescription());
        }
        if (request.getStarRating() != null) {
            hotel.setStarRating(request.getStarRating());
        }
        if (request.getPricePerNight() != null) {
            hotel.setPricePerNight(request.getPricePerNight());
        }
        if (request.getTotalRooms() != null) {
            int bookedRooms = hotel.getTotalRooms() - hotel.getAvailableRooms();
            if (request.getTotalRooms() < bookedRooms) {
                throw new IllegalArgumentException("Cannot reduce total rooms below already booked rooms");
            }
            hotel.setAvailableRooms(request.getTotalRooms() - bookedRooms);
            hotel.setTotalRooms(request.getTotalRooms());
        }
        if (request.getAmenities() != null) {
            hotel.setAmenities(request.getAmenities());
        }
        if (request.getPhoneNumber() != null) {
            hotel.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            hotel.setEmail(request.getEmail());
        }
        if (request.getIsActive() != null) {
            hotel.setIsActive(request.getIsActive());
        }

        Hotel updatedHotel = hotelRepository.save(hotel);
        log.info("Hotel updated successfully with id: {}", updatedHotel.getId());

        return mapToDTO(updatedHotel);
    }

    public void deleteHotel(Long id) {
        log.info("Soft deleting hotel with id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        hotel.setIsActive(false);
        hotelRepository.save(hotel);
        log.info("Hotel soft deleted successfully with id: {}", id);
    }

    public void hardDeleteHotel(Long id) {
        log.info("Hard deleting hotel with id: {}", id);

        if (!hotelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hotel not found with id: " + id);
        }

        hotelRepository.deleteById(id);
        log.info("Hotel hard deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public HotelAvailabilityResponse checkAvailability(Long id, Integer requiredRooms) {
        log.info("Checking availability for hotel {} with {} rooms", id, requiredRooms);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        boolean isAvailable = hotel.getIsActive() && hotel.getAvailableRooms() >= requiredRooms;

        String message = isAvailable
                ? "Hotel has " + hotel.getAvailableRooms() + " rooms available"
                : !hotel.getIsActive()
                    ? "Hotel is currently not active"
                    : "Only " + hotel.getAvailableRooms() + " rooms available";

        return HotelAvailabilityResponse.builder()
                .hotelId(hotel.getId())
                .hotelName(hotel.getName())
                .available(isAvailable)
                .availableRooms(hotel.getAvailableRooms())
                .message(message)
                .build();
    }

    public HotelDTO bookRooms(Long id, Integer numberOfRooms) {
        log.info("Booking {} rooms for hotel {}", numberOfRooms, id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        if (!hotel.getIsActive()) {
            throw new IllegalStateException("Cannot book rooms at an inactive hotel");
        }

        if (hotel.getAvailableRooms() < numberOfRooms) {
            throw new InsufficientRoomsException("Only " + hotel.getAvailableRooms() + " rooms available, requested: " + numberOfRooms);
        }

        hotel.setAvailableRooms(hotel.getAvailableRooms() - numberOfRooms);
        Hotel updatedHotel = hotelRepository.save(hotel);

        log.info("Successfully booked {} rooms for hotel {}", numberOfRooms, id);
        return mapToDTO(updatedHotel);
    }

    public HotelDTO releaseRooms(Long id, Integer numberOfRooms) {
        log.info("Releasing {} rooms for hotel {}", numberOfRooms, id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        int newAvailableRooms = hotel.getAvailableRooms() + numberOfRooms;
        if (newAvailableRooms > hotel.getTotalRooms()) {
            throw new IllegalArgumentException("Cannot release more rooms than total capacity");
        }

        hotel.setAvailableRooms(newAvailableRooms);
        Hotel updatedHotel = hotelRepository.save(hotel);

        log.info("Successfully released {} rooms for hotel {}", numberOfRooms, id);
        return mapToDTO(updatedHotel);
    }

    private HotelDTO mapToDTO(Hotel hotel) {
        return HotelDTO.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .city(hotel.getCity())
                .address(hotel.getAddress())
                .description(hotel.getDescription())
                .starRating(hotel.getStarRating())
                .pricePerNight(hotel.getPricePerNight())
                .totalRooms(hotel.getTotalRooms())
                .availableRooms(hotel.getAvailableRooms())
                .amenities(hotel.getAmenities())
                .phoneNumber(hotel.getPhoneNumber())
                .email(hotel.getEmail())
                .isActive(hotel.getIsActive())
                .createdAt(hotel.getCreatedAt())
                .updatedAt(hotel.getUpdatedAt())
                .build();
    }
}
