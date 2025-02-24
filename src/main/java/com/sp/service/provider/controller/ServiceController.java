package com.sp.service.provider.controller;

import com.sp.service.provider.model.ServiceEntity;
import com.sp.service.provider.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    /**
     * Get all available services (Accessible by all)
     */
    @GetMapping
    public ResponseEntity<List<ServiceEntity>> getAllServices() {
        return ResponseEntity.ok(serviceService.getAllServices());
    }

    /**
     * Get services by category (Accessible by all)
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ServiceEntity>> getServicesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(serviceService.getServicesByCategory(category));
    }

    /**
     * Get services by provider (Accessible by all)
     */
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<ServiceEntity>> getServicesByProvider(@PathVariable Long providerId) {
        return ResponseEntity.ok(serviceService.getServicesByProvider(providerId));
    }

    /**
     * Create a new service (Only Providers & Admins)
     */
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @PostMapping("/{providerId}")
    public ResponseEntity<ServiceEntity> createService(@PathVariable Long providerId, @RequestBody ServiceEntity service) {
        return ResponseEntity.ok(serviceService.addService(service, providerId));
    }

    /**
     * Update a service:
     * - Providers can update their own services
     * - Admins can update any service
     */
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @PutMapping("/{serviceId}/{providerId}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable Long serviceId, @PathVariable Long providerId,
                                                       @RequestBody ServiceEntity updatedService) {
        return ResponseEntity.ok(serviceService.updateService(serviceId, updatedService, providerId));
    }

    /**
     * Delete a service:
     * - Providers can delete their own services
     * - Admins can delete any service
     */
    @PreAuthorize("hasAnyRole('PROVIDER', 'ADMIN')")
    @DeleteMapping("/{serviceId}/{providerId}")
    public ResponseEntity<String> deleteService(@PathVariable Long serviceId, @PathVariable Long providerId) {
        serviceService.deleteService(serviceId, providerId);
        return ResponseEntity.ok("Service deleted successfully!");
    }

    /**
     * Get all services including unavailable ones (Only Admins)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public ResponseEntity<List<ServiceEntity>> getAllServicesAdmin() {
        return ResponseEntity.ok(serviceService.getAllServicesAdmin());
    }
}
