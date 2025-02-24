package com.sp.service.provider.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime bookingTime;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;  // The user who booked the service

    private String customerName;
    private String customerEmail;
    private String serviceType;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private User provider;  // The service provider (Stored as User entity)

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceEntity service;  // The specific service booked

    private double price;
    // Constructors
    public Booking() {}

    public Booking(LocalDateTime bookingTime, BookingStatus status, User customer, String customerName,
                   String customerEmail, User provider, ServiceEntity service, String serviceType, double price) {
        this.bookingTime = bookingTime;
        this.status = status;
        this.customer = customer;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.provider = provider;
        this.service = service;
        this.serviceType = serviceType;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public User getProvider() { return provider; }
    public void setProvider(User provider) { this.provider = provider; }

    public ServiceEntity getService() { return service; }
    public void setService(ServiceEntity service) { this.service = service; }

    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }

    public double getPrice() { return price; }  // Getter for price
    public void setPrice(double price) { this.price = price; }
}
