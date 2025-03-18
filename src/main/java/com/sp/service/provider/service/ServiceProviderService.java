package com.sp.service.provider.service;

import com.sp.service.provider.dto.BookingDTO;
import com.sp.service.provider.dto.ProviderStatsDTO;
import com.sp.service.provider.dto.ReviewDTO;
import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.exceptiom.ResourceNotFoundException;
import com.sp.service.provider.model.Favorite;
import com.sp.service.provider.model.ServiceProvider;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sp.service.provider.specification.ServiceProviderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceProviderService {
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;


    @Transactional(readOnly = true)
    public ProviderStatsDTO getProviderStats(Long providerId) {
        long totalBookings = bookingRepository.countByServiceProviderId(providerId);
        long activeCustomers = bookingRepository.countDistinctUsersByProviderId(providerId);
        double averageRating = reviewRepository.getAverageRatingByProviderId(providerId);
        double revenue = bookingRepository.getTotalRevenueByProviderId(providerId);

        return new ProviderStatsDTO(totalBookings, activeCustomers, averageRating, revenue);
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> getRecentBookings(Long providerId) {
        return bookingRepository.findTop5ByServiceProviderIdOrderByBookingTimeDesc(providerId)
                .stream()
                .map(booking -> new BookingDTO(
                        booking.getId(),
                        booking.getBookingTime(),
                        booking.getCustomer().getId(),
                        booking.getService().getId(),
                        booking.getStatus().name()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getRecentReviews(Long providerId) {
        return reviewRepository.findTop5ByServiceProviderIdOrderByCreatedAtDesc(providerId)
                .stream()
                .map(review -> new ReviewDTO(
                        review.getId(),
                        review.getService().getId(),
                        review.getUser().getId(),
                        review.getUser().getUsername(),
                        review.getRating(),
                        review.getComment(),
                        review.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    public ServiceProvider addServiceProvider(ServiceProviderDTO providerDTO) {
        User user = userRepository.findById(providerDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + providerDTO.getUserId()));

        ServiceProvider provider = new ServiceProvider();
        provider.setServiceType(providerDTO.getServiceType());
        provider.setDescription(providerDTO.getDescription());
        provider.setHourlyRate(providerDTO.getHourlyRate());
        provider.setUser(user);

        return serviceProviderRepository.save(provider);
    }


    /**
     * Add a service provider to the user's favorites list.
     */
    @Transactional
    public String addServiceProviderToFavorites(Long userId, Long serviceProviderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        ServiceProvider serviceProvider = serviceProviderRepository.findById(serviceProviderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Provider not found with id: " + serviceProviderId));

        // Check if the service provider is already in the user's favorites
        if (favoriteRepository.existsByUserIdAndServiceProviderId(userId, serviceProviderId)) {
            return "Service provider is already in your favorites!";
        }

        // Add to favorites
        Favorite favorite = new Favorite(user, serviceProvider);
        favoriteRepository.save(favorite);
        return "Service provider added to favorites!";
    }

    /**
     * Remove a service provider from the user's favorites list.
     */
    @Transactional
    public String removeServiceProviderFromFavorites(Long userId, Long serviceProviderId) {
        Favorite favorite = favoriteRepository.findByUserIdAndServiceProviderId(userId, serviceProviderId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for the given user and service provider"));

        favoriteRepository.delete(favorite);
        return "Service provider removed from favorites!";
    }

    /**
     * Get all favorites for a user
     */
    @Transactional(readOnly = true)
    public List<ServiceProvider> getFavoritesForUser(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        return favorites.stream()
                .map(Favorite::getServiceProvider)
                .collect(Collectors.toList());
    }

    /**
     search by type
     */
    public List<ServiceProvider> findProvidersByServiceType(String serviceType) {
        return serviceProviderRepository.findByServiceType(serviceType);
    }
    /**
     Search providers by city
     */
    public List<ServiceProvider> findProvidersByCity(String city) {
        return serviceProviderRepository.findByCity(city);
    }

    /**
     Search available providers in a city
     */
    public List<ServiceProvider> findAvailableProvidersByCity(String city) {
        return serviceProviderRepository.findByCityAndIsAvailable(city, true);
    }

    /**
     Search providers by city and minimum rating
     */
    public List<ServiceProvider> findProvidersByCityAndRating(String city, double minRating) {
        return serviceProviderRepository.findByCityAndRatingGreaterThanEqual(city, minRating);
    }
    public Page<ServiceProvider> filterServiceProviders(
            String city, String serviceType, Double minRate, Double maxRate, Double minRating, Boolean isAvailable, int page, int size) {

        Specification<ServiceProvider> spec = Specification.where(null);

        if (city != null && !city.isEmpty()) {
            spec = spec.and(ServiceProviderSpecification.hasCity(city));
        }
        if (serviceType != null && !serviceType.isEmpty()) {
            spec = spec.and(ServiceProviderSpecification.hasServiceType(serviceType));
        }
        if (minRate != null || maxRate != null) {
            spec = spec.and(ServiceProviderSpecification.hasHourlyRateBetween(minRate, maxRate));
        }
        if (minRating != null) {
            spec = spec.and(ServiceProviderSpecification.hasRatingAbove(minRating));
        }
        if (isAvailable != null) {
            spec = spec.and(ServiceProviderSpecification.isAvailable(isAvailable));
        }

        Pageable pageable = PageRequest.of(page, size);
        return serviceProviderRepository.findAll(spec, pageable);
    }



}