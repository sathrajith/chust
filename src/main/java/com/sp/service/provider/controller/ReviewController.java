package com.sp.service.provider.controller;

import com.sp.service.provider.dto.ReviewDTO;
import com.sp.service.provider.service.ReviewService;
import com.sp.service.provider.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final JwtUtil jwtUtil;

    public ReviewController(ReviewService reviewService, JwtUtil jwtUtil) {
        this.reviewService = reviewService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * ✅ Get recent reviews for a provider
     */
    @GetMapping("/provider/recent")
    @PreAuthorize("hasRole('SERVICE_PROVIDER')")
    public ResponseEntity<List<ReviewDTO>> getRecentReviews(@RequestHeader("Authorization") String token) {
        Long providerId = extractUserIdFromToken(token);
        return ResponseEntity.ok(reviewService.getRecentReviews(providerId));
    }

    /**
     * ✅ Get all reviews for a specific service
     */
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByService(@PathVariable Long serviceId) {
        return ResponseEntity.ok(reviewService.getReviewsByService(serviceId));
    }

    /**
     * ✅ Get all reviews by a user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    /**
     * ✅ Submit a new review (Only users who booked the service)
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDTO,
                                               @RequestHeader("Authorization") String token) {
        Long userId = extractUserIdFromToken(token);

        return ResponseEntity.ok(reviewService.addReview(reviewDTO, userId));
    }

    /**
     * ✅ Admin deletes an inappropriate review
     */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * ✅ Extract user ID from JWT token
     */
    private Long extractUserIdFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.extractUserId(token);
    }
}
