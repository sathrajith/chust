package com.sp.service.provider.service;

import com.sp.service.provider.model.ServiceEntity;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.ServiceRepository;
import com.sp.service.provider.repository.UserRepository;
import com.sp.service.provider.exceptiom.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieve all services (Accessible by all)
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'PROVIDER')")
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findByIsAvailableTrue();
    }

    /**
     * Retrieve services by category (Accessible by all)
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'PROVIDER')")
    public List<ServiceEntity> getServicesByCategory(String category) {
        return serviceRepository.findByCategory(category);
    }

    /**
     * Retrieve services by provider (Accessible by all)
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'PROVIDER')")
    public List<ServiceEntity> getServicesByProvider(Long providerId) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + providerId));

        return serviceRepository.findByProvider(provider);
    }

    /**
     * Create a new service (Only Providers & Admins)
     */
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Transactional
    public ServiceEntity addService(ServiceEntity service, Long providerId) {
        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + providerId));

        // Check if user has "ROLE_PROVIDER" or "ROLE_ADMIN"
        boolean isProviderOrAdmin = provider.getRoles().stream()
                .anyMatch(role -> role.getName().name().equals("ROLE_PROVIDER") || role.getName().name().equals("ROLE_ADMIN"));

        if (!isProviderOrAdmin) {
            throw new RuntimeException("You do not have permission to create a service.");
        }

        service.setProvider(provider);
        return serviceRepository.save(service);
    }

    /**
     * Update a service:
     * - Providers can update their own services
     * - Admins can update any service
     */
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Transactional
    public ServiceEntity updateService(Long serviceId, ServiceEntity updatedService, Long providerId) {
        ServiceEntity existingService = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        // Check if provider owns the service or is an admin
        boolean isAuthorized = provider.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"))
                || existingService.getProvider().getId().equals(providerId);

        if (!isAuthorized) {
            throw new RuntimeException("You do not have permission to update this service.");
        }

        existingService.setName(updatedService.getName());
        existingService.setDescription(updatedService.getDescription());
        existingService.setPrice(updatedService.getPrice());
        existingService.setCategory(updatedService.getCategory());
        existingService.setAvailable(updatedService.isAvailable());

        return serviceRepository.save(existingService);
    }

    /**
     * Delete a service:
     * - Providers can delete their own services
     * - Admins can delete any service
     */
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @Transactional
    public void deleteService(Long serviceId, Long providerId) {
        ServiceEntity service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with ID: " + serviceId));

        User provider = userRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        // Check if provider owns the service or is an admin
        boolean isAuthorized = provider.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"))
                || service.getProvider().getId().equals(providerId);

        if (!isAuthorized) {
            throw new RuntimeException("You do not have permission to delete this service.");
        }

        serviceRepository.delete(service);
    }

    /**
     * Retrieve ALL services (Only Admins)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<ServiceEntity> getAllServicesAdmin() {
        return serviceRepository.findAll();
    }

}
