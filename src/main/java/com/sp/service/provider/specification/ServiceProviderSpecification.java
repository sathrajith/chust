package com.sp.service.provider.specification;

import com.sp.service.provider.model.ServiceProvider;
import org.springframework.data.jpa.domain.Specification;

public class ServiceProviderSpecification {

    public static Specification<ServiceProvider> hasCity(String city) {
        return (root, query, cb) -> (city == null || city.isEmpty()) ? null : cb.equal(root.get("city"), city);
    }

    public static Specification<ServiceProvider> hasServiceType(String serviceType) {
        return (root, query, cb) -> (serviceType == null || serviceType.isEmpty()) ? null : cb.equal(root.get("serviceType"), serviceType);
    }

    public static Specification<ServiceProvider> hasHourlyRateBetween(Double minRate, Double maxRate) {
        return (root, query, cb) -> {
            if (minRate == null && maxRate == null) return null;
            if (minRate != null && maxRate != null) return cb.between(root.get("hourlyRate"), minRate, maxRate);
            if (minRate != null) return cb.greaterThanOrEqualTo(root.get("hourlyRate"), minRate);
            return cb.lessThanOrEqualTo(root.get("hourlyRate"), maxRate);
        };
    }

    public static Specification<ServiceProvider> hasRatingAbove(Double minRating) {
        return (root, query, cb) -> (minRating == null) ? null : cb.greaterThanOrEqualTo(root.get("averageRating"), minRating);
    }

    public static Specification<ServiceProvider> isAvailable(Boolean available) {
        return (root, query, cb) -> (available == null) ? null : cb.equal(root.get("isAvailable"), available);
    }
}
