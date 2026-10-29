package com.flightreservation.dto;

public class FlightOverviewDto {
    private Long flightId;
    private String flightNumber;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private int capacity;
    private double pricePerSeat;
    private int upcomingJourneys;
    private int upcomingPassengers;
    private int completedJourneys;
    private int completedPassengers;

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public double getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(double pricePerSeat) { this.pricePerSeat = pricePerSeat; }
    public int getUpcomingJourneys() { return upcomingJourneys; }
    public void setUpcomingJourneys(int upcomingJourneys) { this.upcomingJourneys = upcomingJourneys; }
    public int getUpcomingPassengers() { return upcomingPassengers; }
    public void setUpcomingPassengers(int upcomingPassengers) { this.upcomingPassengers = upcomingPassengers; }
    public int getCompletedJourneys() { return completedJourneys; }
    public void setCompletedJourneys(int completedJourneys) { this.completedJourneys = completedJourneys; }
    public int getCompletedPassengers() { return completedPassengers; }
    public void setCompletedPassengers(int completedPassengers) { this.completedPassengers = completedPassengers; }
}