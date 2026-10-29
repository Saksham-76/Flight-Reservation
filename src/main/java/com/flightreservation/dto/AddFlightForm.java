package com.flightreservation.dto;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalTime;

public class AddFlightForm {

    @NotBlank(message = "Flight number is required")
    @Size(max = 50, message = "Flight number can be at most 50 characters")
    @Pattern(regexp = "^[A-Za-z0-9-]*$", message = "Use only letters, digits and hyphen (no spaces)")
    private String flightNumber;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 500, message = "Capacity cannot exceed 500")
    private Integer capacity = 20;   // case study: 20 seats by default

    @NotBlank(message = "Source city is required")
    @Size(max = 50, message = "Source can be at most 50 characters")
    private String source;

    @NotBlank(message = "Destination city is required")
    @Size(max = 50, message = "Destination can be at most 50 characters")
    private String destination;

    @NotNull(message = "Departure time is required")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime departureTime;

    @NotNull(message = "Arrival time is required")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime arrivalTime;

    @NotNull(message = "Price per seat is required")
    @Positive(message = "Price per seat must be greater than 0")
    private Double pricePerSeat;

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public LocalTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public LocalTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public Double getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(Double pricePerSeat) { this.pricePerSeat = pricePerSeat; }
}