package com.flightreservation.service;

import com.flightreservation.dto.FlightDto;

import java.util.List;
import java.util.Optional;

public interface FlightService {

    /** Returns all flights for the given source and destination (case-insensitive). */
    List<FlightDto> searchFlights(String source, String destination);
    FlightDto getFlightById(Long flightId);
    
    Optional<FlightDto> findByFlightNumber(String flightNumber);
    
    FlightDto addFlight(FlightDto flightDetails);
}