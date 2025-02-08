package com.sp.service.provider.controller;

import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.model.ServiceProvider;
import com.sp.service.provider.service.ServiceProviderService;
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

    @PostMapping("/add")
    public ResponseEntity<ServiceProvider> addServiceProvider(@Valid @RequestBody ServiceProviderDTO providerDTO) {
        ServiceProvider provider = serviceProviderService.addServiceProvider(providerDTO);
        return ResponseEntity.ok(provider);
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<ServiceProvider>> searchProviders(@RequestParam String serviceType) {
//        List<ServiceProvider> providers = serviceProviderService.findProvidersByServiceType(serviceType);
//        return ResponseEntity.ok(providers);
//    }



    //search filter service type needed to add...
    @GetMapping("/search")
    public Page<ServiceProvider> searchProviders(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double minRate,
            @RequestParam(required = false) Double maxRate,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "rating") String sortBy, // Default: rating
            @RequestParam(defaultValue = "desc") String sortDirection, // Default: descending
            @RequestParam(defaultValue = "0") int page, // Default: page 0
            @RequestParam(defaultValue = "10") int size // Default: 10 results per page
    ) {
        return serviceProviderService.searchProviders(location, minRate, maxRate, minRating, sortBy, sortDirection, page, size);
    }
}