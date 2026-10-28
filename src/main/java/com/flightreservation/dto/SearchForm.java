package com.flightreservation.dto;

import javax.validation.constraints.NotBlank;

public class SearchForm {

    @NotBlank(message = "Departure city is required")
    private String source;

    @NotBlank(message = "Destination city is required")
    private String destination;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
}