package com.sp.service.provider.service;

import com.sp.service.provider.model.Review;
import com.sp.service.provider.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    public List<Review> getReviewsByProviderId(Long providerId) {
        return reviewRepository.findByProviderId(providerId);
    }

    public Review updateReview(Long id, Review updatedReview) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setRating(updatedReview.getRating());
            review.setComment(updatedReview.getComment());
            return reviewRepository.save(review);
        }
        throw new RuntimeException("Review not found with id: " + id);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}