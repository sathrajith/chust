package com.sp.service.provider.repository;

import com.sp.service.provider.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);

    @Query("SELECT COUNT(DISTINCT b.customer.id) FROM Booking b WHERE b.provider.id = :providerId AND b.status = 'CONFIRMED'")
    long countActiveCustomersForProvider(@Param("providerId") Long providerId);
}