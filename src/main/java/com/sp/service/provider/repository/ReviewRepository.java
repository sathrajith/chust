package com.sp.service.provider.repository;

import com.sp.service.provider.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByServiceId(Long serviceId);
    List<Review> findByUserId(Long userId);

    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.service.provider.id = :providerId")
    double getAverageRatingByProviderId(@Param("providerId") Long providerId);

    @Query("SELECT r FROM Review r WHERE r.service.provider.id = :providerId ORDER BY r.createdAt DESC")
    List<Review> findTop5ByServiceProviderIdOrderByCreatedAtDesc(@Param("providerId") Long providerId);
}