package com.sp.service.provider.service;

import com.sp.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.frontend.url:http://localhost:3000}") // Change this to your frontend URL
    private String frontendUrl;

    /**
     * Send Password Reset Email
     */
    public void sendResetEmail(String to, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        sendEmail(to, "Password Reset Request", "Click the link below to reset your password:\n" + resetLink);
    }

    /**
     * Send Booking Notification (Used for confirmation, cancellation, status updates)
     */
    public void sendBookingNotification(String to, String subject, String message) {
        sendEmail(to, subject, message);
    }

    /**
     * Notify provider of a new booking
     */
    public void sendNewBookingToProvider(String providerEmail, Long bookingId) {
        String message = "You have received a new booking with ID " + bookingId +
                ". Please check your dashboard for details.";
        sendEmail(providerEmail, "New Booking Received", message);
    }

    /**
     * Notify user about booking status changes
     */
    public void sendBookingStatusUpdateToUser(Long userId, Long bookingId, String newStatus) {
        String message = "Your booking with ID " + bookingId + " is now " + newStatus;
        notificationService.sendNotification(userId, message);
        sendEmail(userRepository.findById(userId).get().getEmail(), "Booking Status Update", message);
    }

    /**
     * Notify provider about a new review
     */
    public void sendReviewNotificationToProvider(String providerEmail, Long reviewId) {
        String message = "A new review has been posted for your service. Review ID: " + reviewId;
        sendEmail(providerEmail, "New Review Received", message);
    }

    /**
     * Notify Admin about reported review
     */
    public void sendReportedReviewAlertToAdmin(String adminEmail, Long reviewId) {
        String message = "A review has been reported for inappropriate content. Review ID: " + reviewId +
                ". Please review and take necessary action.";
        sendEmail(adminEmail, "Reported Review Alert", message);
    }

    /**
     * Generic Email Sender
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(text);
        mailSender.send(email);
    }
}
