package com.sp.service.provider.repository;

import com.sp.service.provider.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Check if a user has already favorited a service provider
    boolean existsByUserIdAndServiceProviderId(Long userId, Long serviceProviderId);

    Optional<Favorite> findByUserIdAndServiceProviderId(Long userId, Long serviceProviderId);
    // Find all favorites for a specific user
    List<Favorite> findByUserId(Long userId);
}