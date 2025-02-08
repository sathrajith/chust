package com.sp.service.provider.controller;

import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.model.ServiceProvider;
import com.sp.service.provider.service.ServiceProviderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ServiceProviderController {
    @Autowired
    private ServiceProviderService serviceProviderService;

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

}