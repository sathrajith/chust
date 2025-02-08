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
    @JoinColumn(name = "customer_id")
    private User customer;

    private String customerName;
    private String customerEmail;  // Add this field
    private String serviceProvider;
    private String serviceType;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ServiceProvider provider;

//    @Enumerated(EnumType.STRING)
//    private BookingStatus status;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
    public String getCustomerName(){
        return customerName;
    }
    public void setCustomerName(String customerName){
        this.customerName=customerName;
    }
    public String getCustomerEmail(){
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail){
        this.customerEmail=customerEmail;
    }
    public String getServiceProvider(){
        return serviceProvider;
    }
    public void setServiceProvider(String serviceProvider){
        this.serviceProvider=serviceProvider;
    }
    public String getServiceType(){
        return serviceType;
    }
    public void setServiceType(String serviceType){
        this.serviceType=serviceType;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public ServiceProvider getProvider() {
        return provider;
    }

    public void setProvider(ServiceProvider provider) {
        this.provider = provider;
    }
}
