package com.flightreservation.model;

import java.time.LocalDate;

public class BookingSummary {
    private Long bookingId;
    private String flightNumber;
    private LocalDate journeyDate;
    private String status;

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}