package com.flightreservation.service;

import com.flightreservation.dto.AddFlightForm;
import com.flightreservation.dto.FlightDto;
import com.flightreservation.dto.FlightOverviewDto;
import com.flightreservation.dto.JourneyStatusDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlightService {

    /** Returns all flights for the given source and destination (case-insensitive). */
    List<FlightDto> searchFlights(String source, String destination);
    FlightDto getFlightById(Long flightId);
    
    Optional<FlightDto> findByFlightNumber(String flightNumber);
    
    
    /**
     * Adds a new flight.
     *
     * @throws com.flightreservation.exception.DuplicateResourceException if the flight number exists
     */
    FlightDto addFlight(AddFlightForm form);

    boolean existsByFlightNumber(String flightNumber);

    /** Journeys on or after today that have at least one booked ticket. */
    List<JourneyStatusDto> getUpcomingJourneys(LocalDate today);

    /** Journeys before today that had at least one booked ticket. */
    List<JourneyStatusDto> getCompletedJourneys(LocalDate today);
    /**
     * Every flight in the database (with or without bookings), optionally filtered by
     * flight number, source or destination, plus upcoming/completed journey counts.
     */
    List<FlightOverviewDto> getAllFlights(String query, LocalDate today);
}