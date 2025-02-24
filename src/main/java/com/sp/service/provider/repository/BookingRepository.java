package com.sp.service.provider.repository;

import com.sp.service.provider.model.Booking;
import com.sp.service.provider.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerId(Long customerId);
    List<Booking> findByProviderId(Long providerId);
    boolean existsByCustomerIdAndProviderIdAndBookingTime(Long customerId, Long providerId, LocalDateTime bookingTime);
    List<Booking> findByStatus(BookingStatus status);
    long countByServiceProviderId(Long providerId);

    @Query("SELECT COUNT(DISTINCT b.customer) FROM Booking b WHERE b.provider.id = :providerId")
    long countDistinctUsersByProviderId(@Param("providerId") Long providerId);

    @Query("SELECT COALESCE(SUM(b.price), 0) FROM Booking b WHERE b.provider.id = :providerId")
    double getTotalRevenueByProviderId(@Param("providerId") Long providerId);

    List<Booking> findTop5ByServiceProviderIdOrderByBookingTimeDesc(Long providerId);
}