package com.sp.service.provider.model;

import jakarta.persistence.*;

@Entity
@Table(name = "service_provider")
public class ServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String city;

    @Column(nullable = false)
    private String serviceType;
    private String description;
    private double hourlyRate;
    private double averageRating;
    private double rating;
    private boolean isAvailable;




    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    //constructors

    // Default constructor (needed for JPA)
    public ServiceProvider() {}

    // All-args constructor
    public ServiceProvider(Long id, String name, String city, String serviceType, String description,
                           double hourlyRate, double averageRating, int rating, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.serviceType = serviceType;
        this.description = description;
        this.hourlyRate = hourlyRate;
        this.averageRating = averageRating;
        this.rating = rating;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getRating(){
        return rating;
    }
    public void setRating(double rating){
        this.rating=rating;
    }
    public boolean isAvailable(){
        return isAvailable;
    }
    public void setAvailable(boolean isAvailable){
        this.isAvailable=isAvailable;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
