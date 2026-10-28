package com.flightreservation.dto;

import java.time.LocalDate;
import java.util.List;

public class BookingSummary {

    private Long bookingId;

    private String flightNumber;

    private String source;

    private String destination;

    private LocalDate journeyDate;

    private int passengerCount;

    private double totalPrice;

    private List<PassengerForm> passengers;

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<PassengerForm> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerForm> passengers) {
        this.passengers = passengers;
    }
}