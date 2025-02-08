package com.sp.service.provider.specification;

import com.sp.service.provider.model.ServiceProvider;
import org.springframework.data.jpa.domain.Specification;

public class ServiceProviderSpecification {

    public static Specification<ServiceProvider> hasLocation(String location) {
        return (root, query, criteriaBuilder) ->
                location == null ? null : criteriaBuilder.equal(root.get("location"), location);
    }

    public static Specification<ServiceProvider> hasHourlyRateBetween(Double minRate, Double maxRate) {
        return (root, query, criteriaBuilder) -> {
            if (minRate == null && maxRate == null) return null;
            if (minRate != null && maxRate != null)
                return criteriaBuilder.between(root.get("hourlyRate"), minRate, maxRate);
            if (minRate != null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("hourlyRate"), minRate);
            return criteriaBuilder.lessThanOrEqualTo(root.get("hourlyRate"), maxRate);
        };
    }

    public static Specification<ServiceProvider> hasMinRating(Double minRating) {
        return (root, query, criteriaBuilder) ->
                minRating == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("averageRating"), minRating);
    }
}

