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

import com.flightreservation.dto.AddFlightForm;
import com.flightreservation.dto.JourneyStatusDto;
import com.flightreservation.exception.DuplicateResourceException;
import com.flightreservation.model.FlightJourneySummary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import java.time.LocalDate;

import com.flightreservation.dto.FlightOverviewDto;
import com.flightreservation.model.FlightOverview;

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

    private static final String INSERT_FLIGHT =
            "INSERT INTO flight (flight_number, source, destination, departure_time, "
          + "arrival_time, capacity, price_per_seat) VALUES "
          + "(:flightNumber, :source, :destination, :departureTime, :arrivalTime, :capacity, :pricePerSeat)";
    private static final String COUNT_BY_NUMBER =
            "SELECT COUNT(*) FROM flight WHERE flight_number = :flightNumber";

    // Only flights with at least one BOOKED ticket appear; revenue = booked tickets x seat price
    private static final String JOURNEY_SELECT =
            "SELECT f.flight_number, b.journey_date, "
          + "COUNT(t.pnr_no) AS total_passengers, "
          + "COUNT(DISTINCT b.booking_id) AS total_bookings, "
          + "COUNT(t.pnr_no) * f.price_per_seat AS total_revenue "
          + "FROM booking b "
          + "JOIN flight f ON f.flight_id = b.flight_id "
          + "JOIN ticket t ON t.booking_id = b.booking_id "
          + "WHERE t.status = 'BOOKED' ";
    private static final String JOURNEY_GROUP =
            " GROUP BY f.flight_id, f.flight_number, f.price_per_seat, b.journey_date ";
    private static final String UPCOMING_JOURNEYS = JOURNEY_SELECT
          + "AND b.journey_date >= :today" + JOURNEY_GROUP
          + "ORDER BY b.journey_date, f.flight_number";
    private static final String COMPLETED_JOURNEYS = JOURNEY_SELECT
          + "AND b.journey_date < :today" + JOURNEY_GROUP
          + "ORDER BY b.journey_date DESC, f.flight_number";

    // LEFT JOINs keep flights that were never booked; only BOOKED tickets are counted,
    // so cancelled tickets do not inflate the numbers.
    private static final String ALL_FLIGHTS_OVERVIEW =
            "SELECT f.flight_id, f.flight_number, f.source, f.destination, f.departure_time, "
          + "f.arrival_time, f.capacity, f.price_per_seat, "
          + "COUNT(DISTINCT CASE WHEN t.pnr_no IS NOT NULL AND b.journey_date >= :today "
          + "                    THEN b.journey_date END) AS upcoming_journeys, "
          + "COUNT(CASE WHEN t.pnr_no IS NOT NULL AND b.journey_date >= :today THEN 1 END) AS upcoming_passengers, "
          + "COUNT(DISTINCT CASE WHEN t.pnr_no IS NOT NULL AND b.journey_date < :today "
          + "                    THEN b.journey_date END) AS completed_journeys, "
          + "COUNT(CASE WHEN t.pnr_no IS NOT NULL AND b.journey_date < :today THEN 1 END) AS completed_passengers "
          + "FROM flight f "
          + "LEFT JOIN booking b ON b.flight_id = f.flight_id "
          + "LEFT JOIN ticket t ON t.booking_id = b.booking_id AND t.status = 'BOOKED' "
          + "WHERE f.flight_number LIKE :pattern OR f.source LIKE :pattern OR f.destination LIKE :pattern "
          + "GROUP BY f.flight_id "
          + "ORDER BY f.flight_number";

    

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
    public FlightDto addFlight(AddFlightForm form) {
        Flight flight = flightMapper.toEntity(form);

        if (existsByFlightNumber(flight.getFlightNumber())) {
            throw new DuplicateResourceException("Flight number " + flight.getFlightNumber() + " already exists");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbc.update(INSERT_FLIGHT, new BeanPropertySqlParameterSource(flight), keyHolder);
        } catch (DuplicateKeyException e) {
            // two admins adding the same number at the same moment: the UNIQUE key wins
            throw new DuplicateResourceException("Flight number " + flight.getFlightNumber() + " already exists");
        }
        flight.setFlightId(keyHolder.getKey().longValue());
        return flightMapper.toDto(flight);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByFlightNumber(String flightNumber) {
        Integer count = jdbc.queryForObject(COUNT_BY_NUMBER,
                Map.of("flightNumber", flightNumber.trim().toUpperCase()), Integer.class);
        return count != null && count > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<JourneyStatusDto> getUpcomingJourneys(LocalDate today) {
        List<FlightJourneySummary> rows = jdbc.query(UPCOMING_JOURNEYS, Map.of("today", today),
                new BeanPropertyRowMapper<>(FlightJourneySummary.class));
        return flightMapper.toJourneyDtoList(rows, today);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JourneyStatusDto> getCompletedJourneys(LocalDate today) {
        List<FlightJourneySummary> rows = jdbc.query(COMPLETED_JOURNEYS, Map.of("today", today),
                new BeanPropertyRowMapper<>(FlightJourneySummary.class));
        return flightMapper.toJourneyDtoList(rows, today);
    }
    @Override
    @Transactional(readOnly = true)
    public List<FlightOverviewDto> getAllFlights(String query, LocalDate today) {
        String text = (query == null) ? "" : query.trim();
        // escape LIKE wildcards typed by the user so "50%" is searched literally
        String escaped = text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");

        List<FlightOverview> rows = jdbc.query(ALL_FLIGHTS_OVERVIEW,
                Map.of("today", today, "pattern", "%" + escaped + "%"),
                new BeanPropertyRowMapper<>(FlightOverview.class));
        return flightMapper.toOverviewDtoList(rows);
    }
}