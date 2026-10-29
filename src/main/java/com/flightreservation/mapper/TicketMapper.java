package com.flightreservation.mapper;

import com.flightreservation.dto.TicketDto;
import com.flightreservation.model.Ticket;
import com.flightreservation.util.AppConstants;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class TicketMapper {

    /** Ticket entity -> TicketDto. A ticket can be cancelled while BOOKED and not yet flown. */
    public TicketDto toDto(Ticket ticket, LocalDate today) {
        if (ticket == null) {
            return null;
        }
        TicketDto dto = new TicketDto();
        dto.setPnrNo(ticket.getPnrNo());
        dto.setPassengerName(ticket.getPassengerName());
        dto.setGender(ticket.getGender());
        dto.setAge(ticket.getAge());
        dto.setJourneyDate(ticket.getJourneyDate());
        dto.setSeatNo(ticket.getSeatNo());
        dto.setBookingId(ticket.getBookingId());
        dto.setStatus(ticket.getStatus());
        dto.setCancellable(AppConstants.STATUS_BOOKED.equals(ticket.getStatus())
                && !ticket.getJourneyDate().isBefore(today));
        return dto;
    }

    public List<TicketDto> toDtoList(List<Ticket> tickets, LocalDate today) {
        return tickets.stream().map(t -> toDto(t, today)).toList();
    }
}