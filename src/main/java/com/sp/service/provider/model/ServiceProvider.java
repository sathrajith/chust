package com.sp.service.provider.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "service_provider")
public class ServiceProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private String serviceType; // e.g., Electrician, Cleaner, Tutor
    private String description;
    private double hourlyRate;
    private double averageRating;

//    @OneToMany(mappedBy = "serviceProvider", cascade = CascadeType.ALL)
//    private List<Review> reviews;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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
//    public void setReviews(List<Review> reviews){
//        this.reviews=reviews;
//    }


    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id=id;
    }
    public String getServiceType(){
        return serviceType;
    }
    public void setServiceType(String serviceType){
        this.serviceType=serviceType;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public User getUser(){
        return user;
    }
    public void setUser(User user){
        this.user=user;
    }
    public double getHourlyRate(){
        return hourlyRate;
    }
    public void setHourlyRate(double hourlyRate){
        this.hourlyRate=hourlyRate;
    }
}
