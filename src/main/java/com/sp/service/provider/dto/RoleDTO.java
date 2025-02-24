package com.sp.service.provider.dto;

import jakarta.validation.constraints.NotNull;

public class RoleDTO {
    private Long id;

    @NotNull(message = "Role name cannot be null")
    private String name;

    // Constructors
    public RoleDTO() {}

    public RoleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
