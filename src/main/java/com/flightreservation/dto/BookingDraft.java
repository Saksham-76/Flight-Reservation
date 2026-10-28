package com.flightreservation.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingDraft {

    private Long flightId;

    private String flightNumber;

    private String source;

    private String destination;

    private LocalDate journeyDate;

    private int passengerCount;

    private List<PassengerForm> passengers = new ArrayList<>();

    private int currentPassengerIndex;

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
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

    public List<PassengerForm> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<PassengerForm> passengers) {
        this.passengers = passengers;
    }

    public int getCurrentPassengerIndex() {
        return currentPassengerIndex;
    }

    public void setCurrentPassengerIndex(int currentPassengerIndex) {
        this.currentPassengerIndex = currentPassengerIndex;
    }
}