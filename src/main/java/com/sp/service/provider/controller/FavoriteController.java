package com.sp.service.provider.controller;

import com.sp.service.provider.model.ServiceProvider;
import com.sp.service.provider.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private ServiceProviderService serviceProviderService;

    // Add service provider to favorites
    @PostMapping("/add")
    public ResponseEntity<String> addToFavorites(@RequestParam Long userId, @RequestParam Long serviceProviderId) {
        String message = serviceProviderService.addServiceProviderToFavorites(userId, serviceProviderId);
        return ResponseEntity.ok(message);
    }

    // Remove service provider from favorites
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromFavorites(@RequestParam Long userId, @RequestParam Long serviceProviderId) {
        String message = serviceProviderService.removeServiceProviderFromFavorites(userId, serviceProviderId);
        return ResponseEntity.ok(message);
    }

    // Get all favorites for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ServiceProvider>> getFavorites(@PathVariable Long userId) {
        List<ServiceProvider> favorites = serviceProviderService.getFavoritesForUser(userId);
        return ResponseEntity.ok(favorites);
    }
}

