package com.flightreservation.service.impl;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingSummary;
import com.flightreservation.dto.PassengerForm;
import com.flightreservation.service.BookingService;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class BookingServiceImpl implements BookingService {

    private final NamedParameterJdbcTemplate jdbc;

    public BookingServiceImpl(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public BookingSummary confirmBooking(BookingDraft draft, Long userId) {

        // Validate basic booking information.
        if (draft == null || userId == null || draft.getFlightId() == null) {
            throw new IllegalArgumentException("Invalid booking details.");
        }

        LocalDate journeyDate = draft.getJourneyDate();
        List<PassengerForm> passengers = draft.getPassengers();

        if (journeyDate == null || journeyDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Journey date must be today or a future date.");
        }

        if (passengers == null || passengers.isEmpty()
                || passengers.size() > 10
                || passengers.size() != draft.getPassengerCount()) {
            throw new IllegalArgumentException(
                    "Passenger count must be between 1 and 10.");
        }

        // Lock the flight row to serialize bookings for this flight.
        List<Map<String, Object>> flights = jdbc.queryForList(
                "SELECT flight_id, flight_number, source, destination, "
                        + "capacity, price_per_seat "
                        + "FROM flight WHERE flight_id = :flightId FOR UPDATE",
                Map.of("flightId", draft.getFlightId())
        );

        if (flights.isEmpty()) {
            throw new IllegalArgumentException("Selected flight does not exist.");
        }

        Map<String, Object> flight = flights.get(0);

        String flightNumber = (String) flight.get("flight_number");
        String source = (String) flight.get("source");
        String destination = (String) flight.get("destination");
        int capacity = ((Number) flight.get("capacity")).intValue();
        double pricePerSeat =
                ((Number) flight.get("price_per_seat")).doubleValue();

        // Validate passenger details before inserting records.
        for (PassengerForm passenger : passengers) {
            if (passenger == null
                    || passenger.getPassengerName() == null
                    || passenger.getPassengerName().trim().isEmpty()
                    || passenger.getGender() == null
                    || passenger.getGender().trim().isEmpty()
                    || passenger.getAge() == null
                    || passenger.getAge() < 0
                    || passenger.getAge() > 120) {

                throw new IllegalArgumentException(
                        "Please provide valid details for every passenger.");
            }

            if (passenger.getPassengerName().trim().length() > 50
                    || passenger.getGender().trim().length() > 50) {
                throw new IllegalArgumentException(
                        "Passenger name or gender exceeds the allowed length.");
            }
        }

        // Calculate seats already booked for this flight and date.
        Integer bookedSeats = jdbc.queryForObject(
                "SELECT COALESCE(SUM(seats), 0) "
                        + "FROM booking "
                        + "WHERE flight_id = :flightId "
                        + "AND journey_date = :journeyDate "
                        + "AND status = 'BOOKED'",
                Map.of(
                        "flightId", draft.getFlightId(),
                        "journeyDate", Date.valueOf(journeyDate)
                ),
                Integer.class
        );

        int alreadyBooked = bookedSeats == null ? 0 : bookedSeats;
        int requestedSeats = passengers.size();

        if (requestedSeats > capacity - alreadyBooked) {
            throw new IllegalArgumentException(
                    "Not enough seats available for the selected journey date.");
        }

        double totalPrice = pricePerSeat * requestedSeats;

        // Insert the booking and retrieve its generated ID.
        String bookingSql =
                "INSERT INTO booking "
                        + "(booking_date, journey_date, seats, total_price, "
                        + "status, flight_id, user_id) "
                        + "VALUES (:bookingDate, :journeyDate, :seats, "
                        + ":totalPrice, 'BOOKED', :flightId, :userId)";

        MapSqlParameterSource bookingParams = new MapSqlParameterSource()
                .addValue("bookingDate", Date.valueOf(LocalDate.now()))
                .addValue("journeyDate", Date.valueOf(journeyDate))
                .addValue("seats", requestedSeats)
                .addValue("totalPrice", totalPrice)
                .addValue("flightId", draft.getFlightId())
                .addValue("userId", userId);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(bookingSql, bookingParams, keyHolder, new String[]{"booking_id"});

        Number generatedId = keyHolder.getKey();

        if (generatedId == null) {
            throw new IllegalStateException(
                    "Unable to retrieve the booking ID.");
        }

        long bookingId = generatedId.longValue();

        // Insert one ticket for each passenger.
        String ticketSql =
                "INSERT INTO ticket "
                        + "(passenger_name, gender, age, journey_date, "
                        + "seat_no, booking_id, status) "
                        + "VALUES (:name, :gender, :age, :journeyDate, "
                        + ":seatNo, :bookingId, 'BOOKED')";

        for (int i = 0; i < passengers.size(); i++) {
            PassengerForm passenger = passengers.get(i);

            MapSqlParameterSource ticketParams =
                    new MapSqlParameterSource()
                            .addValue("name",
                                    passenger.getPassengerName().trim())
                            .addValue("gender",
                                    passenger.getGender().trim())
                            .addValue("age", passenger.getAge())
                            .addValue("journeyDate", Date.valueOf(journeyDate))
                            .addValue("seatNo", "S" + (i + 1))
                            .addValue("bookingId", bookingId);

            jdbc.update(ticketSql, ticketParams);
        }

        // Prepare the confirmation summary.
        BookingSummary summary = new BookingSummary();
        summary.setBookingId(bookingId);
        summary.setFlightNumber(flightNumber);
        summary.setSource(source);
        summary.setDestination(destination);
        summary.setJourneyDate(journeyDate);
        summary.setPassengerCount(requestedSeats);
        summary.setTotalPrice(totalPrice);
        summary.setPassengers(passengers);

        return summary;
    }
}