package com.sp.service.provider.controller;

import com.sp.service.provider.model.Booking;
import com.sp.service.provider.model.BookingStatus;
import com.sp.service.provider.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * Create a new booking (Only Users can book services)
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{userId}/create")
    public ResponseEntity<Booking> createBooking(@PathVariable Long userId, @RequestBody Booking booking) {
        Booking newBooking = bookingService.createBooking(booking, userId);
        return ResponseEntity.ok(newBooking);
    }

    /**
     * Get a booking by ID (Users can see their bookings, Providers can see their assigned ones)
     */
    @PreAuthorize("hasRole('USER') or hasRole('PROVIDER')")
    @GetMapping("/{bookingId}/{userId}")
    public ResponseEntity<Optional<Booking>> getBookingById(@PathVariable Long bookingId, @PathVariable Long userId) {
        Optional<Booking> booking = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(booking);
    }

    /**
     * Get all bookings for a customer (Only the customer can see their bookings)
     */
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Booking>> getBookingsByCustomer(@PathVariable Long customerId) {
        List<Booking> bookings = bookingService.getBookingsByCustomerId(customerId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get all bookings for a provider (Only the provider can see their assigned bookings)
     */
    @PreAuthorize("hasRole('PROVIDER')")
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Booking>> getBookingsByProvider(@PathVariable Long providerId) {
        List<Booking> bookings = bookingService.getBookingsByProviderId(providerId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Update Booking Status (Only Providers can accept/reject their own bookings)
     */
    @PreAuthorize("hasRole('PROVIDER')")
    @PutMapping("/{bookingId}/status/{providerId}")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long bookingId, @PathVariable Long providerId,
                                                       @RequestParam BookingStatus status) {
        Booking updatedBooking = bookingService.updateBookingStatus(bookingId, providerId, status);
        return ResponseEntity.ok(updatedBooking);
    }

    /**
     * Cancel a booking (Only Users can cancel their bookings)
     */
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{bookingId}/cancel/{userId}")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId, @PathVariable Long userId) {
        bookingService.cancelBooking(bookingId, userId);
        return ResponseEntity.ok("Booking canceled successfully.");
    }

    /**
     * Get all bookings (Only Admins can view all)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
}
