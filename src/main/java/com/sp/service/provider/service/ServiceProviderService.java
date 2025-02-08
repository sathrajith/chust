package com.sp.service.provider.service;

import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.exceptiom.ResourceNotFoundException;
import com.sp.service.provider.model.ServiceProvider;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.ServiceProviderRepository;
import com.sp.service.provider.repository.UserRepository;
import com.sp.service.provider.specification.ServiceProviderSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceProviderService {
    @Autowired
    private ServiceProviderRepository serviceProviderRepository;

    @Autowired
    private UserRepository userRepository;



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

    public List<ServiceProvider> findProvidersByServiceType(String serviceType) {
        return serviceProviderRepository.findByServiceType(serviceType);
    }


    //search by service type need to add....
    public Page<ServiceProvider> searchProviders(
            String location, Double minRate, Double maxRate, Double minRating,
            String sortBy, String sortDirection, int page, int size) {

        // Build dynamic query specifications
        Specification<ServiceProvider> spec = Specification
                .where(ServiceProviderSpecification.hasLocation(location))
                .and(ServiceProviderSpecification.hasHourlyRateBetween(minRate, maxRate))
                .and(ServiceProviderSpecification.hasMinRating(minRating));

        // Configure sorting
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy != null ? sortBy : "id"); // Default sort by ID

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        // Execute query
        return serviceProviderRepository.findAll(spec, pageable);
    }
}