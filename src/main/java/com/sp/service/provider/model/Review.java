package com.sp.service.provider.model;

import jakarta.persistence.*;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    private ServiceProvider provider;

    //private Long providerId;

//    @ManyToOne
//    @JoinColumn(name = "customer_id")
//    private User customer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

////    public Long getProviderId() {
////        return providerId;
////    }
////
////    public void setProviderId(Long providerId) {
////        this.providerId = providerId;
////    }
//
//    public User getCustomer() {
//        return customer;
//    }
//
//    public void setCustomer(Long customerId) {
//        this.customer = customer;
//    }
}
