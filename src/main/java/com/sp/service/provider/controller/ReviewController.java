package com.sp.service.provider.controller;

import com.sp.service.provider.model.Review;
import com.sp.service.provider.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        return ResponseEntity.ok(createdReview);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

//    @GetMapping("/provider/{providerId}")
//    public ResponseEntity<List<Review>> getReviewsByProviderId(@PathVariable Long providerId) {
//        List<Review> reviews = reviewService.getReviewsByProviderId(providerId);
//        return ResponseEntity.ok(reviews);
//    }
//
//    @GetMapping("/customer/{customerId}")
//    public ResponseEntity<List<Review>> getReviewsByCustomerId(@PathVariable Long customerId) {
//        List<Review> reviews = reviewService.getReviewsByCustomerId(customerId);
//        return ResponseEntity.ok(reviews);
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Review> updateReview(@PathVariable Long id, @RequestBody Review updatedReview) {
        Review review = reviewService.updateReview(id, updatedReview);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}