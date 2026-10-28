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

@Service
public class FlightServiceImpl implements FlightService {

    private static final String SEARCH_BY_ROUTE =
            "SELECT flight_id, flight_number, source, destination, departure_time, "
          + "arrival_time, capacity, price_per_seat "
          + "FROM flight WHERE source = :source AND destination = :destination "
          + "ORDER BY departure_time";

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
}