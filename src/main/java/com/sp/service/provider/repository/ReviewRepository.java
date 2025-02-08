package com.sp.service.provider.repository;

import com.sp.service.provider.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
//    List<Review> findByProviderId(Long providerId);
//    List<Review> findByCustomerId(Long customerId);
}