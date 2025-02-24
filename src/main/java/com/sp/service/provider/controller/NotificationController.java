package com.sp.service.provider.controller;

import com.sp.service.provider.model.Notification;
import com.sp.service.provider.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * Send notification (Admin only)
     */
    @PostMapping("/send/{userId}")
    public ResponseEntity<String> sendNotification(@PathVariable Long userId, @RequestParam String message) {
        notificationService.sendNotification(userId, message);
        return ResponseEntity.ok("Notification sent!");
    }

    /**
     * Get unread notifications for a user
     */
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    /**
     * Mark a notification as read
     */
    @PostMapping("/mark-read/{notificationId}")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read!");
    }
}
