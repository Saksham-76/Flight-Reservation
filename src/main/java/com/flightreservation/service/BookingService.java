package com.flightreservation.service;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingDto;
import com.flightreservation.dto.BookingSummaryDto;
import com.flightreservation.dto.TicketDto;

import java.time.LocalDate;
import java.util.List;

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
    
    /** All bookings of this user only, newest first. */
    List<BookingSummaryDto> getBookingsForUser(long userId);

    /** Tickets of one booking, only if the booking belongs to the user. */
    List<TicketDto> getTickets(long bookingId, long userId);

    /**
     * Cancels the whole booking and all its tickets (their seats become available again).
     *
     * @throws com.flightreservation.exception.BookingNotCancellableException if already cancelled or in the past
     */
    void cancelBooking(long bookingId, long userId);

    /** Cancels one ticket; if it was the last active ticket, the booking becomes CANCELLED too. */
    void cancelTicket(long bookingId, long pnrNo, long userId);
}