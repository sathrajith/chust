package com.sp.service.provider.dto;

public class ProviderStatsDTO {
    private long totalBookings;
    private long activeCustomers;
    private double averageRating;
    private double revenue;

    public ProviderStatsDTO(long totalBookings, long activeCustomers, double averageRating, double revenue) {
        this.totalBookings = totalBookings;
        this.activeCustomers = activeCustomers;
        this.averageRating = averageRating;
        this.revenue = revenue;
    }




    public long getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(long totalBookings) {
        this.totalBookings = totalBookings;
    }

    // Getter and Setter for activeCustomers
    public long getActiveCustomers() {
        return activeCustomers;
    }

    public void setActiveCustomers(long activeCustomers) {
        this.activeCustomers = activeCustomers;
    }

    // Getter and Setter for averageRating
    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    // Getter and Setter for revenue
    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

}
