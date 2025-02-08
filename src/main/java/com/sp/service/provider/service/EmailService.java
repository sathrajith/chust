package com.sp.service.provider.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}") // Change this to your frontend URL
    private String frontendUrl;

    public void sendResetEmail(String to, String token) {
        String resetLink = frontendUrl + "/reset-password?token=" + token;
        sendEmail(to, "Password Reset Request", "Click the link below to reset your password:\n" + resetLink);
    }

    public void sendBookingNotification(String to, String subject, String message) {
        sendEmail(to, subject, message);
    }

    private void sendEmail(String to, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
    }
}
