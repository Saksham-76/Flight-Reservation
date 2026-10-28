package com.flightreservation.service;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingSummary;

public interface BookingService {

    /**
     * Validates the booking and saves the booking and passenger details.
     *
     * @param draft completed booking details
     * @param userId ID of the logged-in customer
     * @return summary of the confirmed booking
     */
    BookingSummary confirmBooking(BookingDraft draft, Long userId);
}