package com.sp.service.provider.service;

import com.sp.service.provider.model.Notification;
import com.sp.service.provider.model.User;
import com.sp.service.provider.repository.NotificationRepository;
import com.sp.service.provider.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // WebSocket Messaging

    /**
     * Send a notification to a user
     */
    public void sendNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user); // Fix: Set user correctly
        notification.setMessage(message);
        notification.setRead(false);

        notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        messagingTemplate.convertAndSend("/topic/notifications/" + userId, message);
    }

    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId); // Fixed method name
    }

    /**
     * Mark a notification as read
     */
    public void markNotificationAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }
}
