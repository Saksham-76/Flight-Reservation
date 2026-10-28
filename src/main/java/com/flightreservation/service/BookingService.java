package com.flightreservation.service;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingDto;

import java.time.LocalDate;

public interface BookingService {

    /** Seats still free on a flight for a given journey date. */
    int getAvailableSeats(long flightId, int capacity, LocalDate journeyDate);

    /**
     * Creates the booking and one ticket per passenger in a single transaction.
     *
     * @return the new booking id
     * @throws com.flightreservation.exception.SeatsUnavailableException if the seats were taken meanwhile
     */
    long createBooking(BookingDraft draft, long userId);

    /** Returns a booking, only if it belongs to the given user. */
    BookingDto getBooking(long bookingId, long userId);
}