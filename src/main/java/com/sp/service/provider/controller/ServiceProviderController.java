package com.sp.service.provider.controller;

import com.sp.service.provider.dto.BookingDTO;
import com.sp.service.provider.dto.ProviderStatsDTO;
import com.sp.service.provider.dto.ReviewDTO;
import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.model.ServiceProvider;
import com.sp.service.provider.service.ServiceProviderService;
import com.sp.service.provider.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ServiceProviderController {
    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/add")
    public ResponseEntity<ServiceProvider> addServiceProvider(@Valid @RequestBody ServiceProviderDTO providerDTO) {
        ServiceProvider provider = serviceProviderService.addServiceProvider(providerDTO);
        return ResponseEntity.ok(provider);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ServiceProvider>> searchProviders(@RequestParam String serviceType) {
        List<ServiceProvider> providers = serviceProviderService.findProvidersByServiceType(serviceType);
        return ResponseEntity.ok(providers);
    }
    // Get providers by city
    @GetMapping("/city/{city}")
    public List<ServiceProvider> getProvidersByCity(@PathVariable String city) {
        return serviceProviderService.findProvidersByCity(city);
    }

    // Get available providers in a city
    @GetMapping("/city/{city}/available")
    public List<ServiceProvider> getAvailableProvidersByCity(@PathVariable String city) {
        return serviceProviderService.findAvailableProvidersByCity(city);
    }

    // Get providers by city and minimum rating
    @GetMapping("/city/{city}/rating/{rating}")
    public List<ServiceProvider> getProvidersByCityAndRating(@PathVariable String city, @PathVariable double rating) {
        return serviceProviderService.findProvidersByCityAndRating(city, rating);
    }

    @GetMapping("/filter")
    public Page<ServiceProvider> filterProviders(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) Double minRate,
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return serviceProviderService.filterServiceProviders(city, serviceType, minRate, maxRate, minRating, isAvailable, page, size);
    }

    /**
     * ✅ Get provider statistics (total bookings, revenue, active customers, rating)
     */
    @GetMapping("/stats")
    public ResponseEntity<ProviderStatsDTO> getProviderStats(@RequestHeader("Authorization") String token) {
        Long providerId = extractUserIdFromToken(token);
        return ResponseEntity.ok(serviceProviderService.getProviderStats(providerId));
    }

    /**
     * ✅ Get recent bookings for a provider
     */
    @GetMapping("/bookings/recent")
    public ResponseEntity<List<BookingDTO>> getRecentBookings(@RequestHeader("Authorization") String token) {
        Long providerId = extractUserIdFromToken(token);
        return ResponseEntity.ok(serviceProviderService.getRecentBookings(providerId));
    }

    /**
     * ✅ Get recent reviews for a provider
     */
    @GetMapping("/reviews/recent")
    public ResponseEntity<List<ReviewDTO>> getRecentReviews(@RequestHeader("Authorization") String token) {
        Long providerId = extractUserIdFromToken(token);
        return ResponseEntity.ok(serviceProviderService.getRecentReviews(providerId));
    }

    /**
     * ✅ Extract user ID from JWT token
     */
    private Long extractUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.extractUserId(token);
    }
}