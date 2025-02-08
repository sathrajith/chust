package com.sp.service.provider.repository;

import com.sp.service.provider.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

    List<ServiceProvider> findByCity(String city); // Find providers by city

    List<ServiceProvider> findByCityAndIsAvailable(String city, boolean isAvailable); // Find available providers in a city

    List<ServiceProvider> findByCityAndRatingGreaterThanEqual(String city, double rating); // Find providers with min rating

    List<ServiceProvider> findByServiceType(String serviceType);
}
