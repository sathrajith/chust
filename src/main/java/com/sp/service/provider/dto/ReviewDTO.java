package com.sp.service.provider.dto;

import com.sp.service.provider.model.User;

public class ReviewDTO {
    private Long id;
    private int rating;
    private String comment;
    //private Long providerId;
    //private User customer;

    // Getters and Setters
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

//    public Long getProviderId() {
//        return providerId;
//    }
//
//    public void setProviderId(Long providerId) {
//        this.providerId = providerId;
//    }
//
//    public User getCustomer() {
//        return customer;
//    }
//
//    public void setCustomer(User customer) {
//        this.customer = customer;
//    }
}