package com.sp.service.provider.service;

import com.sp.service.provider.dto.ReviewDTO;
import com.sp.service.provider.exceptiom.ResourceNotFoundException;
import com.sp.service.provider.model.Review;
import com.sp.service.provider.model.ServiceEntity;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.ReviewRepository;
import com.sp.service.provider.repository.ServiceRepository;
import com.sp.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Submit a new review (Only Users who booked the service)
     */
    @PreAuthorize("hasRole('USER')")
    @Transactional
    public ReviewDTO addReview(ReviewDTO reviewDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceEntity service = serviceRepository.findById(reviewDTO.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        // Check if the user has booked this service before reviewing
        boolean hasBooked = service.getBookings().stream()
                .anyMatch(booking -> booking.getCustomer().getId().equals(userId));

        if (!hasBooked) {
            throw new IllegalStateException("You can only review services you have booked.");
        }

        // Create review entity
        Review review = new Review();
        review.setUser(user);
        review.setService(service);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        Review savedReview = reviewRepository.save(review);

        return new ReviewDTO(
                savedReview.getId(),
                service.getId(),
                user.getId(),
                user.getUsername(),
                savedReview.getRating(),
                savedReview.getComment(),
                savedReview.getCreatedAt()
        );
    }

    /**
     * Get all reviews for a service
     */
    public List<ReviewDTO> getReviewsByService(Long serviceId) {
        List<Review> reviews = reviewRepository.findByServiceId(serviceId);
        return reviews.stream().map(review -> new ReviewDTO(
                review.getId(),
                review.getService().getId(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        )).collect(Collectors.toList());
    }

    /**
     * Get all reviews by a user
     */
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream().map(review -> new ReviewDTO(
                review.getId(),
                review.getService().getId(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        )).collect(Collectors.toList());
    }

    /**
     * Admin can delete inappropriate reviews
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        reviewRepository.delete(review);
    }

    /**
     * ✅ Get recent reviews for a provider
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getRecentReviews(Long providerId) {
        List<Review> reviews = reviewRepository.findTop5ByServiceProviderIdOrderByCreatedAtDesc(providerId);
        return reviews.stream().map(review -> new ReviewDTO(
                review.getId(),
                review.getService().getId(),
                review.getUser().getId(),
                review.getUser().getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        )).collect(Collectors.toList());
    }
}
