package com.flightreservation.service.impl;

import com.flightreservation.dto.FlightDto;
import com.flightreservation.mapper.FlightMapper;
import com.flightreservation.model.Flight;
import com.flightreservation.service.FlightService;


import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FlightServiceImpl implements FlightService {

    private static final String SEARCH_BY_ROUTE =
            "SELECT flight_id, flight_number, source, destination, departure_time, "
          + "arrival_time, capacity, price_per_seat "
          + "FROM flight WHERE source = :source AND destination = :destination "
          + "ORDER BY departure_time";
    private static final String FIND_BY_NUMBER =
            "SELECT flight_id, flight_number, source, destination, departure_time, "
          + "arrival_time, capacity, price_per_seat FROM flight WHERE flight_number = :flightNumber";

    

    private final NamedParameterJdbcTemplate jdbc;
    private final FlightMapper flightMapper;

    public FlightServiceImpl(NamedParameterJdbcTemplate jdbc, FlightMapper flightMapper) {
        this.jdbc = jdbc;
        this.flightMapper = flightMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlightDto> searchFlights(String source, String destination) {
        List<Flight> flights = jdbc.query(SEARCH_BY_ROUTE,
                Map.of("source", source.trim(), "destination", destination.trim()),
                new BeanPropertyRowMapper<>(Flight.class));
        return flightMapper.toDtoList(flights);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FlightDto getFlightById(Long flightId) {

        List<Flight> flights = jdbc.query(
                "SELECT flight_id, flight_number, source, destination, "
                        + "departure_time, arrival_time, capacity, price_per_seat "
                        + "FROM flight WHERE flight_id = :flightId",
                Map.of("flightId", flightId),
                new BeanPropertyRowMapper<>(Flight.class)
        );

        if (flights.isEmpty()) {
            return null;
        }

        return flightMapper.toDtoList(flights).get(0);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<FlightDto> findByFlightNumber(String flightNumber) {
        return jdbc.query(FIND_BY_NUMBER,
                        Map.of("flightNumber", flightNumber.trim()),
                        new BeanPropertyRowMapper<>(Flight.class))
                .stream().findFirst().map(flightMapper::toDto);
    }
    
    @Override
    @Transactional
    public FlightDto addFlight(FlightDto flightDetails) {

        if (flightDetails == null) {
            throw new IllegalArgumentException("Flight details are required.");
        }

        if (flightDetails.getFlightNumber() == null
                || flightDetails.getFlightNumber().trim().isEmpty()
                || flightDetails.getSource() == null
                || flightDetails.getSource().trim().isEmpty()
                || flightDetails.getDestination() == null
                || flightDetails.getDestination().trim().isEmpty()
                || flightDetails.getDepartureTime() == null
                || flightDetails.getDepartureTime().trim().isEmpty()
                || flightDetails.getArrivalTime() == null
                || flightDetails.getArrivalTime().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "All flight details must be provided.");
        }

        if (flightDetails.getSource().trim()
                .equalsIgnoreCase(flightDetails.getDestination().trim())) {
            throw new IllegalArgumentException(
                    "Source and destination cannot be the same.");
        }

        if (flightDetails.getPricePerSeat() <= 0
                || Double.isNaN(flightDetails.getPricePerSeat())
                || Double.isInfinite(flightDetails.getPricePerSeat())) {
            throw new IllegalArgumentException(
                    "Price per seat must be greater than zero.");
        }

        String sql =
                "INSERT INTO flight "
                        + "(flight_number, source, destination, departure_time, "
                        + "arrival_time, capacity, price_per_seat) "
                        + "VALUES (:flightNumber, :source, :destination, "
                        + ":departureTime, :arrivalTime, :capacity, :pricePerSeat)";

        Map<String, Object> params = Map.of(
                "flightNumber", flightDetails.getFlightNumber().trim(),
                "source", flightDetails.getSource().trim(),
                "destination", flightDetails.getDestination().trim(),
                "departureTime", flightDetails.getDepartureTime().trim(),
                "arrivalTime", flightDetails.getArrivalTime().trim(),
                "capacity", 20,
                "pricePerSeat", flightDetails.getPricePerSeat()
        );

        jdbc.update(sql, params);

        return flightDetails;
    }
}