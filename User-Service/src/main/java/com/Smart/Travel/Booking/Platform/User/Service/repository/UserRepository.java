package com.Smart.Travel.Booking.Platform.User.Service.repository;

import com.Smart.Travel.Booking.Platform.User.Service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    List<User> findByIsActiveTrue();
    
    Optional<User> findByIdAndIsActiveTrue(Long id);
}
