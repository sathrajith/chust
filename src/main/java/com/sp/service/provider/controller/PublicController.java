package com.sp.service.provider.controller;


import com.sp.service.provider.dto.ReviewDTO;
import com.sp.service.provider.dto.ServiceProviderDTO;
import com.sp.service.provider.model.ServiceEntity;
import com.sp.service.provider.service.ReviewService;
import com.sp.service.provider.service.ServiceService;
import com.sp.service.provider.service.UserService;
import com.sp.service.provider.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {



    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ServiceService serviceService;

//    @Autowired
//    private JwtUtil jwtUtil;

    @GetMapping("/providers")
    public ResponseEntity<List<ServiceProviderDTO>> getServiceProviders() {
        List<ServiceProviderDTO> providers = userService.getServiceProviders();
        return ResponseEntity.ok(providers);
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getReviewsByService(serviceId));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ServiceEntity>> getServicesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(serviceService.getServicesByCategory(category));
    }

//    public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDTO,
//                                               @RequestHeader("Authorization") String token) {
//        Long userId = extractUserIdFromToken(token);
//
//        return ResponseEntity.ok(reviewService.addReview(reviewDTO, userId));
//    }
//
//    private Long extractUserIdFromToken(String token) {
//        if (token.startsWith("Bearer ")) {
//            token = token.substring(7);
//        }
//        return jwtUtil.extractUserId(token);
//    }
}
