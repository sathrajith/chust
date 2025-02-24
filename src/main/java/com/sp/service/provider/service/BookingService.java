package com.sp.service.provider.service;

import com.sp.service.provider.dto.BookingStatusUpdate;
import com.sp.service.provider.model.Booking;
import com.sp.service.provider.model.BookingStatus;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.BookingRepository;
import com.sp.service.provider.repository.UserRepository;
import com.sp.service.provider.exceptiom.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    public BookingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Send a real-time update via WebSocket
     */
    public void sendBookingStatusUpdate(Long bookingId, BookingStatus status) {
        messagingTemplate.convertAndSend("/topic/booking-status", new BookingStatusUpdate(bookingId, status.name()));
    }

    /**
     * Create a new booking (Only Users can book services)
     */
    @PreAuthorize("hasRole('USER')")
    public Booking createBooking(Booking booking, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Ensure only users (not providers) can create a booking
        boolean isUser = user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_USER"));
        if (!isUser) {
            throw new RuntimeException("Only users can create bookings.");
        }

        booking.setCustomer(user);
        booking.setStatus(BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);

        // Notify customer
        emailService.sendBookingNotification(
                user.getEmail(),
                "Booking Confirmation",
                "Your booking with ID " + savedBooking.getId() + " has been created successfully."
        );

        return savedBooking;
    }

    /**
     * Get a booking by ID (Users can see their bookings, Providers can see their assigned ones)
     */
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    public Optional<Booking> getBookingById(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("You do not have permission to access this booking.");
        }

        return Optional.of(booking);
    }

    /**
     * Get bookings for a specific customer (Only that customer can see them)
     */
    @PreAuthorize("#customerId == authentication.principal.id")
    public List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    /**
     * Get bookings for a specific provider (Only the provider can see them)
     */
    @PreAuthorize("hasRole('PROVIDER')")
    public List<Booking> getBookingsByProviderId(Long providerId) {
        return bookingRepository.findByProviderId(providerId);
    }

    /**
     * Update Booking Status (Only Providers can accept/reject their own bookings)
     */
    @Transactional
    @PreAuthorize("hasRole('PROVIDER')")
    public Booking updateBookingStatus(Long bookingId, Long providerId, BookingStatus newStatus) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Ensure the provider is only updating their assigned bookings
        if (!booking.getService().getProvider().getId().equals(providerId)) {
            throw new RuntimeException("You can only update your own bookings.");
        }

        // Validate status transitions
        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of completed/cancelled bookings");
        }

        if (booking.getStatus() == BookingStatus.PENDING && (newStatus == BookingStatus.ACCEPTED || newStatus == BookingStatus.CANCELLED)) {
            booking.setStatus(newStatus);
        } else if (booking.getStatus() == BookingStatus.ACCEPTED && newStatus == BookingStatus.COMPLETED) {
            booking.setStatus(newStatus);
        } else {
            throw new IllegalStateException("Invalid status transition");
        }

        bookingRepository.save(booking);
        sendBookingStatusUpdate(bookingId, newStatus);

        // Send email notification
        emailService.sendBookingNotification(
                booking.getCustomer().getEmail(),
                "Booking Status Update",
                "Your booking with ID " + booking.getId() + " is now " + newStatus.name()
        );

        return booking;
    }

    /**
     * Cancel a booking (Only Users can cancel their bookings)
     */
    @Transactional
    @PreAuthorize("hasRole('USER')")
    public void cancelBooking(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getCustomer().getId().equals(userId)) {
            throw new RuntimeException("You do not have permission to cancel this booking.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        sendBookingStatusUpdate(id, BookingStatus.CANCELLED);

        emailService.sendBookingNotification(
                booking.getCustomer().getEmail(),
                "Booking Canceled",
                "Your booking with ID " + booking.getId() + " has been canceled."
        );
    }

    /**
     * Get all bookings (Only Admins can view all)
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
