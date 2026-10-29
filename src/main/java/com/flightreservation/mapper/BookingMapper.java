package com.flightreservation.mapper;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingDto;
import com.flightreservation.dto.BookingForm;
import com.flightreservation.dto.FlightDto;
import com.flightreservation.dto.PassengerForm;
import com.flightreservation.model.Booking;
import com.flightreservation.model.Flight;
import com.flightreservation.model.Ticket;
import com.flightreservation.util.AppConstants;
import org.springframework.stereotype.Component;

import com.flightreservation.dto.BookingSummaryDto;
import com.flightreservation.model.BookingSummary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    /** FlightDto + BookingForm -> BookingDraft (kept in the session between screens). */
    public BookingDraft toDraft(FlightDto flight, BookingForm form) {
        BookingDraft draft = new BookingDraft();
        draft.setFlightId(flight.getFlightId());
        draft.setFlightNumber(flight.getFlightNumber());
        draft.setJourneyDate(form.getJourneyDate());
        draft.setNumberOfPassengers(form.getNumberOfPassengers());
        return draft;
    }

    /** BookingDraft + Flight entity -> Booking entity. */
    public Booking toEntity(BookingDraft draft, Flight flight, long userId) {
        Booking booking = new Booking();
        booking.setBookingDate(LocalDate.now());
        booking.setJourneyDate(draft.getJourneyDate());
        booking.setSeats(draft.getNumberOfPassengers());
        booking.setTotalPrice(flight.getPricePerSeat() * draft.getNumberOfPassengers());
        booking.setStatus(AppConstants.STATUS_BOOKED);
        booking.setFlightId(flight.getFlightId());
        booking.setUserId(userId);
        return booking;
    }

    /** One PassengerForm -> one Ticket entity, with the seat allocated for that passenger. */
    public List<Ticket> toTicketEntities(BookingDraft draft, long bookingId, List<String> seatNumbers) {
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < draft.getPassengers().size(); i++) {
            PassengerForm p = draft.getPassengers().get(i);
            Ticket ticket = new Ticket();
            ticket.setPassengerName(p.getPassengerName().trim());
            ticket.setGender(p.getGender());
            ticket.setAge(p.getAge());
            ticket.setJourneyDate(draft.getJourneyDate());
            ticket.setSeatNo(seatNumbers.get(i));
            ticket.setBookingId(bookingId);
            ticket.setStatus(AppConstants.STATUS_BOOKED);
            tickets.add(ticket);
        }
        return tickets;
    }

    /** Booking + Flight entities -> BookingDto. */
    public BookingDto toDto(Booking booking, Flight flight) {
        BookingDto dto = new BookingDto();
        dto.setBookingId(booking.getBookingId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setSource(flight.getSource());
        dto.setDestination(flight.getDestination());
        dto.setJourneyDate(booking.getJourneyDate());
        dto.setSeats(booking.getSeats());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        return dto;
    }
    
    /** Booking list row -> BookingSummaryDto, deciding whether the Cancel button is active. */
    public BookingSummaryDto toSummaryDto(BookingSummary row, LocalDate today) {
        BookingSummaryDto dto = new BookingSummaryDto();
        dto.setBookingId(row.getBookingId());
        dto.setFlightNumber(row.getFlightNumber());
        dto.setJourneyDate(row.getJourneyDate());
        dto.setStatus(row.getStatus());
        dto.setCancellable(AppConstants.STATUS_BOOKED.equals(row.getStatus())
                && !row.getJourneyDate().isBefore(today));
        return dto;
    }

    public List<BookingSummaryDto> toSummaryDtoList(List<BookingSummary> rows, LocalDate today) {
        return rows.stream().map(r -> toSummaryDto(r, today)).toList();
    }
}