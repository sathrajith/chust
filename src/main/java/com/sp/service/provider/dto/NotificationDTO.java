package com.sp.service.provider.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Message cannot be blank")
    private String message;

    private boolean read;
    private LocalDateTime timestamp;

    // Constructors
    public NotificationDTO() {}

    public NotificationDTO(Long id, Long userId, String message, boolean read, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.read = read;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
