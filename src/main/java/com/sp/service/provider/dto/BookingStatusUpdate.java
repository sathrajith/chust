package com.sp.service.provider.dto;

public class BookingStatusUpdate {
    private Long bookingId;
    private String status;

    public Long getBookingId(){
        return bookingId;
    }
    public void setBookingId(Long bookingId){
        this.bookingId=bookingId;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status=status;
    }

    public BookingStatusUpdate(Long bookingId, String status) {
        this.bookingId = bookingId;
        this.status = status;
    }
}
