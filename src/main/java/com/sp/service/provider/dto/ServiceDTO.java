package com.sp.service.provider.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ServiceDTO {
    private Long id;

    @NotBlank(message = "Service name cannot be blank")
    private String name;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @Min(value = 0, message = "Price must be a positive value")
    private double price;

    private boolean isAvailable;

    // Constructors
    public ServiceDTO() {}

    public ServiceDTO(Long id, String name, String category, String description, Long providerId, double price, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.providerId = providerId;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getProviderId() { return providerId; }
    public void setProviderId(Long providerId) { this.providerId = providerId; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
