package com.sp.service.provider.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BookingDTO {
    private Long id;

    @NotNull(message = "Booking time cannot be null")
    @FutureOrPresent(message = "Booking time must be in the present or future")
    private LocalDateTime bookingTime;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Service ID is required")
    private Long serviceId;

    private String status;

    // Constructors
    public BookingDTO() {}

    public BookingDTO(Long id, LocalDateTime bookingTime, Long customerId, Long serviceId, String status) {
        this.id = id;
        this.bookingTime = bookingTime;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.status = status;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
