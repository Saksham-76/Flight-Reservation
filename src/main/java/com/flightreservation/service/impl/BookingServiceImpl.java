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

import com.flightreservation.dto.BookingSummaryDto;
import com.flightreservation.dto.TicketDto;
import com.flightreservation.exception.BookingNotCancellableException;
import com.flightreservation.mapper.TicketMapper;
import com.flightreservation.model.BookingSummary;
import com.flightreservation.util.AppConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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

    private static final String LIST_BOOKINGS =
            "SELECT b.booking_id, f.flight_number, b.journey_date, b.status "
          + "FROM booking b JOIN flight f ON f.flight_id = b.flight_id "
          + "WHERE b.user_id = :userId ORDER BY b.booking_id DESC";

    private static final String FIND_TICKETS =
            "SELECT t.pnr_no, t.passenger_name, t.gender, t.age, t.journey_date, t.seat_no, "
          + "t.booking_id, t.status FROM ticket t JOIN booking b ON b.booking_id = t.booking_id "
          + "WHERE t.booking_id = :bookingId AND b.user_id = :userId ORDER BY t.pnr_no";

    private static final String LOCK_BOOKING = FIND_BOOKING + " FOR UPDATE";

    private static final String CANCEL_ALL_TICKETS =
            "UPDATE ticket SET status = 'CANCELLED' WHERE booking_id = :bookingId AND status = 'BOOKED'";
    private static final String CANCEL_TICKET =
            "UPDATE ticket SET status = 'CANCELLED' "
          + "WHERE pnr_no = :pnrNo AND booking_id = :bookingId AND status = 'BOOKED'";
    private static final String COUNT_ACTIVE_TICKETS =
            "SELECT COUNT(*) FROM ticket WHERE booking_id = :bookingId AND status = 'BOOKED'";
    private static final String CANCEL_BOOKING =
            "UPDATE booking SET status = 'CANCELLED' WHERE booking_id = :bookingId";
    
    
    private final NamedParameterJdbcTemplate jdbc;
    private final BookingMapper bookingMapper;
    private final TicketMapper ticketMapper;

    private static final Logger log = LoggerFactory.getLogger(BookingServiceImpl.class);
    
    public BookingServiceImpl(NamedParameterJdbcTemplate jdbc, BookingMapper bookingMapper,
                              TicketMapper ticketMapper) {
        this.jdbc = jdbc;
        this.bookingMapper = bookingMapper;
        this.ticketMapper = ticketMapper;
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
        
        log.info("Booking {} created: flight {} on {}, {} passenger(s), user {}",
                bookingId, flight.getFlightNumber(), draft.getJourneyDate(),
                draft.getNumberOfPassengers(), userId);
        
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
    
    @Override
    @Transactional(readOnly = true)
    public List<BookingSummaryDto> getBookingsForUser(long userId) {
        List<BookingSummary> rows = jdbc.query(LIST_BOOKINGS, Map.of("userId", userId),
                new BeanPropertyRowMapper<>(BookingSummary.class));
        return bookingMapper.toSummaryDtoList(rows, LocalDate.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getTickets(long bookingId, long userId) {
        List<Ticket> tickets = jdbc.query(FIND_TICKETS,
                Map.of("bookingId", bookingId, "userId", userId),
                new BeanPropertyRowMapper<>(Ticket.class));
        if (tickets.isEmpty()) {
            throw new ResourceNotFoundException("Booking not found");
        }
        return ticketMapper.toDtoList(tickets, LocalDate.now());
    }

    @Override
    @Transactional
    public void cancelBooking(long bookingId, long userId) {
        lockCancellableBooking(bookingId, userId);
        jdbc.update(CANCEL_ALL_TICKETS, Map.of("bookingId", bookingId));
        jdbc.update(CANCEL_BOOKING, Map.of("bookingId", bookingId));
        log.info("Booking {} cancelled by user {}", bookingId, userId);
    }

    @Override
    @Transactional
    public void cancelTicket(long bookingId, long pnrNo, long userId) {
        lockCancellableBooking(bookingId, userId);

        int updated = jdbc.update(CANCEL_TICKET, Map.of("pnrNo", pnrNo, "bookingId", bookingId));
        if (updated == 0) {
        	log.info("Ticket {} of booking {} cancelled by user {}", pnrNo, bookingId, userId);
            throw new BookingNotCancellableException("This ticket is already cancelled or does not exist.");
        }

        Integer active = jdbc.queryForObject(COUNT_ACTIVE_TICKETS,
                Map.of("bookingId", bookingId), Integer.class);
        if (active != null && active == 0) {
            jdbc.update(CANCEL_BOOKING, Map.of("bookingId", bookingId));
        }
    }

    /**
     * Loads the booking with a row lock (so two cancellations cannot interleave) and checks
     * that it belongs to the user, is still BOOKED and the journey is not in the past.
     */
    private Booking lockCancellableBooking(long bookingId, long userId) {
        Booking booking = jdbc.query(LOCK_BOOKING, Map.of("bookingId", bookingId, "userId", userId),
                        new BeanPropertyRowMapper<>(Booking.class))
                .stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!AppConstants.STATUS_BOOKED.equals(booking.getStatus())) {
            throw new BookingNotCancellableException("This booking is already cancelled.");
        }
        if (booking.getJourneyDate().isBefore(LocalDate.now())) {
            throw new BookingNotCancellableException("A journey that has already taken place cannot be cancelled.");
        }
        return booking;
    }
}