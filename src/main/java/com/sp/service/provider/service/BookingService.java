package com.sp.service.provider.service;

import com.sp.service.provider.dto.BookingStatusUpdate;
import com.sp.service.provider.model.Booking;
import com.sp.service.provider.model.BookingStatus;
import com.sp.service.provider.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;


    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public BookingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendBookingStatusUpdate(Long bookingId, String status) {
        messagingTemplate.convertAndSend("/topic/booking-status", new BookingStatusUpdate(bookingId, status));
    }

    @Autowired
    private EmailService emailService;

    public Booking createBooking(Booking booking) {
        Booking savedBooking = bookingRepository.save(booking);
        emailService.sendBookingNotification(
                booking.getCustomerEmail(),
                "Booking Confirmation",
                "Your booking with ID " + booking.getId() + " has been created successfully."
        );
        return savedBooking;
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByCustomerId(Long customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<Booking> getBookingsByProviderId(Long providerId) {
        return bookingRepository.findByProviderId(providerId);
    }

    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.COMPLETED || booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of completed/cancelled bookings");
        }

        booking.setStatus(status);
        bookingRepository.save(booking);

        // Send email notification
        emailService.sendBookingNotification(
                booking.getCustomerEmail(),
                "Booking Status Update",
                "Your booking with ID " + booking.getId() + " is now " + status
        );

        return booking;
    }

    public void deleteBooking(Long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        if (optionalBooking.isPresent()) {
            Booking booking = optionalBooking.get();
            bookingRepository.deleteById(id);

            emailService.sendBookingNotification(
                    booking.getCustomerEmail(),
                    "Booking Canceled",
                    "Your booking with ID " + booking.getId() + " has been canceled."
            );
        }
    }
}
