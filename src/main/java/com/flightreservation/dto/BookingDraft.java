package com.flightreservation.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDraft implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long flightId;
    private String flightNumber;
    private LocalDate journeyDate;
    private int numberOfPassengers;
    private List<PassengerForm> passengers = new ArrayList<>();

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public int getNumberOfPassengers() { return numberOfPassengers; }
    public void setNumberOfPassengers(int numberOfPassengers) { this.numberOfPassengers = numberOfPassengers; }
    public List<PassengerForm> getPassengers() { return passengers; }
    public void setPassengers(List<PassengerForm> passengers) { this.passengers = passengers; }
}