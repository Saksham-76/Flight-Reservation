package com.flightreservation.dto;

import java.time.LocalDate;

public class JourneyStatusDto {
    private String flightNumber;
    private LocalDate journeyDate;
    private int totalPassengers;
    private int totalBookings;
    private double totalRevenue;
    private long daysUntilJourney;

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public int getTotalPassengers() { return totalPassengers; }
    public void setTotalPassengers(int totalPassengers) { this.totalPassengers = totalPassengers; }
    public int getTotalBookings() { return totalBookings; }
    public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public long getDaysUntilJourney() { return daysUntilJourney; }
    public void setDaysUntilJourney(long daysUntilJourney) { this.daysUntilJourney = daysUntilJourney; }
}