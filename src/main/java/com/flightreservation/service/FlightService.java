package com.flightreservation.service;

import com.flightreservation.dto.FlightDto;

import java.util.List;

public interface FlightService {

    /** Returns all flights for the given source and destination (case-insensitive). */
    List<FlightDto> searchFlights(String source, String destination);
    FlightDto getFlightById(Long flightId);
}