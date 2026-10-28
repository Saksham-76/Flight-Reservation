package com.flightreservation.service.impl;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingDto;
import com.flightreservation.exception.ResourceNotFoundException;
import com.flightreservation.exception.SeatsUnavailableException;
import com.flightreservation.mapper.BookingMapper;
import com.flightreservation.model.Booking;
import com.flightreservation.model.Flight;
import com.flightreservation.model.Ticket;
import com.flightreservation.service.BookingService;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class BookingServiceImpl implements BookingService {

    private static final String FLIGHT_COLUMNS =
            "SELECT flight_id, flight_number, source, destination, departure_time, "
          + "arrival_time, capacity, price_per_seat FROM flight WHERE flight_id = :flightId";

    private static final String FIND_FLIGHT = FLIGHT_COLUMNS;
    private static final String LOCK_FLIGHT = FLIGHT_COLUMNS + " FOR UPDATE";

    private static final String TAKEN_FILTER =
            " FROM ticket t JOIN booking b ON t.booking_id = b.booking_id "
          + "WHERE b.flight_id = :flightId AND b.journey_date = :journeyDate AND t.status = 'BOOKED'";
    private static final String COUNT_TAKEN = "SELECT COUNT(*)" + TAKEN_FILTER;
    private static final String TAKEN_SEATS = "SELECT t.seat_no" + TAKEN_FILTER;

    private static final String INSERT_BOOKING =
            "INSERT INTO booking (booking_date, journey_date, seats, total_price, status, flight_id, user_id) "
          + "VALUES (:bookingDate, :journeyDate, :seats, :totalPrice, :status, :flightId, :userId)";
    private static final String INSERT_TICKET =
            "INSERT INTO ticket (passenger_name, gender, age, journey_date, seat_no, booking_id, status) "
          + "VALUES (:passengerName, :gender, :age, :journeyDate, :seatNo, :bookingId, :status)";

    private static final String FIND_BOOKING =
            "SELECT booking_id, booking_date, journey_date, seats, total_price, status, flight_id, user_id "
          + "FROM booking WHERE booking_id = :bookingId AND user_id = :userId";

    private final NamedParameterJdbcTemplate jdbc;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(NamedParameterJdbcTemplate jdbc, BookingMapper bookingMapper) {
        this.jdbc = jdbc;
        this.bookingMapper = bookingMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public int getAvailableSeats(long flightId, int capacity, LocalDate journeyDate) {
        Integer taken = jdbc.queryForObject(COUNT_TAKEN,
                Map.of("flightId", flightId, "journeyDate", journeyDate), Integer.class);
        return Math.max(capacity - (taken == null ? 0 : taken), 0);
    }

    @Override
    @Transactional
    public long createBooking(BookingDraft draft, long userId) {
        // 1. Lock the flight row. This MUST be the first statement of the transaction:
        //    concurrent bookings of the same flight now wait here, and the plain SELECTs
        //    below (which take their snapshot after the lock) see the previous booking.
        Flight flight = jdbc.query(LOCK_FLIGHT, Map.of("flightId", draft.getFlightId()),
                        new BeanPropertyRowMapper<>(Flight.class))
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        // 2. Re-check availability inside the transaction
        List<String> takenSeats = jdbc.queryForList(TAKEN_SEATS,
                Map.of("flightId", flight.getFlightId(), "journeyDate", draft.getJourneyDate()),
                String.class);
        int available = flight.getCapacity() - takenSeats.size();
        if (available < draft.getNumberOfPassengers()) {
            throw new SeatsUnavailableException("Only " + Math.max(available, 0)
                    + " seat(s) are left on this flight for the selected date.");
        }

        // 3. Insert the booking
        Booking booking = bookingMapper.toEntity(draft, flight, userId);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(INSERT_BOOKING, new BeanPropertySqlParameterSource(booking), keyHolder);
        long bookingId = keyHolder.getKey().longValue();

        // 4. Allocate seats and insert one ticket per passenger
        List<String> seatNumbers = allocateSeats(flight.getCapacity(), takenSeats,
                draft.getNumberOfPassengers());
        List<Ticket> tickets = bookingMapper.toTicketEntities(draft, bookingId, seatNumbers);
        jdbc.batchUpdate(INSERT_TICKET, SqlParameterSourceUtils.createBatch(tickets));

        return bookingId;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = jdbc.query(FIND_BOOKING, Map.of("bookingId", bookingId, "userId", userId),
                        new BeanPropertyRowMapper<>(Booking.class))
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Flight flight = jdbc.query(FIND_FLIGHT, Map.of("flightId", booking.getFlightId()),
                        new BeanPropertyRowMapper<>(Flight.class))
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

        return bookingMapper.toDto(booking, flight);
    }

    /** Picks the first free seats. Seats freed by cancellations are reused. */
    private List<String> allocateSeats(int capacity, List<String> takenSeats, int needed) {
        Set<String> taken = new HashSet<>(takenSeats);
        List<String> seats = new ArrayList<>();
        for (int i = 1; i <= capacity && seats.size() < needed; i++) {
            String label = seatLabel(i);
            if (!taken.contains(label)) {
                seats.add(label);
            }
        }
        return seats;
    }

    /** 1 -> A1 ... 6 -> A6, 7 -> B1 ... (six seats per row). */
    private String seatLabel(int index) {
        char row = (char) ('A' + (index - 1) / 6);
        int col = (index - 1) % 6 + 1;
        return row + String.valueOf(col);
    }
}