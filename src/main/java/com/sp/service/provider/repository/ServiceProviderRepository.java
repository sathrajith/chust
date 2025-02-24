package com.sp.service.provider.repository;

import com.sp.service.provider.model.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long>, JpaSpecificationExecutor<ServiceProvider> {




    List<ServiceProvider> findByCity(String city); // Find providers by city

    List<ServiceProvider> findByCityAndIsAvailable(String city, boolean isAvailable); // Find available providers in a city

    List<ServiceProvider> findByCityAndRatingGreaterThanEqual(String city, double rating); // Find providers with min rating

    List<ServiceProvider> findByServiceType(String serviceType);

//    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.city = :city AND sp.averageRating >= :rating")
//    List<ServiceProvider> findByCityAndAverageRatingGreaterThanEqual(@Param("city") String city, @Param("rating") double rating);
//
//    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.city = :city AND sp.serviceType = :serviceType AND sp.hourlyRate BETWEEN :minRate AND :maxRate AND sp.available = :available")
//    List<ServiceProvider> findByCityAndServiceTypeAndHourlyRateBetweenAndIsAvailable(
//            @Param("city") String city,
//            @Param("serviceType") String serviceType,
//            @Param("minRate") double minRate,
//            @Param("maxRate") double maxRate,
//            @Param("available") boolean available
//    );

}
