package com.sp.service.provider.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime bookingTime;
    private String status; // PENDING, CONFIRMED, COMPLETED

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ServiceProvider provider;


    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public LocalDateTime getBookingTime(){
        return bookingTime;
    }
    public void setBookingTime(LocalDateTime bookingTime){
        this.bookingTime=bookingTime;
    }
    public User getCustomer(){
        return customer;
    }
    public void setCustomer(User customer){
        this.customer=customer;
    }
    public ServiceProvider getProvider(){
        return provider;
    }
    public void setProvider(ServiceProvider provider){
        this.provider=provider;
    }

}
