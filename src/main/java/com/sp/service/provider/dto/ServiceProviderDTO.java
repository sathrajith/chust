package com.sp.service.provider.dto;

import com.sp.service.provider.model.Review;
import jakarta.validation.constraints.*;

import java.util.List;

public class ServiceProviderDTO {
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String location;


    @NotBlank(message = "Service type is required")
    private String serviceType;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Hourly rate must be a positive value")
    private double hourlyRate;

//    private List<Review> reviews;

    private double averageRating;

    @NotNull(message = "User ID is required")
    private Long userId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getLocation(){
        return location;
    }
    public void setLocation(String location){
        this.location=location;
    }
    public double getAverageRating(){
        return averageRating;
    }
    public void setAverageRating(double averageRating){
        this.averageRating=averageRating;
    }
//    public List<Review> getReviews(){
//        return reviews;
//    }
//    public void setReviews(){
//        this.reviews=reviews;
//    }
}