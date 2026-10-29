package com.flightreservation.mapper;

import com.flightreservation.dto.FlightDto;
import com.flightreservation.model.Flight;
import org.springframework.stereotype.Component;

import java.util.List;

import com.flightreservation.dto.AddFlightForm;
import com.flightreservation.dto.JourneyStatusDto;
import com.flightreservation.model.FlightJourneySummary;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.flightreservation.dto.FlightOverviewDto;
import com.flightreservation.model.FlightOverview;

@Component
public class FlightMapper {
	
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /** Flight entity -> FlightDto. */
    public FlightDto toDto(Flight flight) {
        if (flight == null) {
            return null;
        }
        FlightDto dto = new FlightDto();
        dto.setFlightId(flight.getFlightId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setSource(flight.getSource());
        dto.setDestination(flight.getDestination());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setPricePerSeat(flight.getPricePerSeat());
        dto.setCapacity(flight.getCapacity());
        return dto;
    }

    public List<FlightDto> toDtoList(List<Flight> flights) {
        return flights.stream().map(this::toDto).toList();
    }
    
    /** AddFlightForm -> Flight entity (flight number upper-cased, times stored as HH:mm text). */
    public Flight toEntity(AddFlightForm form) {
        if (form == null) {
            return null;
        }
        Flight flight = new Flight();
        flight.setFlightNumber(form.getFlightNumber().trim().toUpperCase());
        flight.setSource(form.getSource().trim());
        flight.setDestination(form.getDestination().trim());
        flight.setDepartureTime(form.getDepartureTime().format(TIME_FORMAT));
        flight.setArrivalTime(form.getArrivalTime().format(TIME_FORMAT));
        flight.setCapacity(form.getCapacity());
        flight.setPricePerSeat(form.getPricePerSeat());
        return flight;
    }

    /** Aggregate row -> JourneyStatusDto, adding "days until journey" relative to today. */
    public JourneyStatusDto toJourneyDto(FlightJourneySummary row, LocalDate today) {
        JourneyStatusDto dto = new JourneyStatusDto();
        dto.setFlightNumber(row.getFlightNumber());
        dto.setJourneyDate(row.getJourneyDate());
        dto.setTotalPassengers(row.getTotalPassengers());
        dto.setTotalBookings(row.getTotalBookings());
        dto.setTotalRevenue(row.getTotalRevenue());
        dto.setDaysUntilJourney(ChronoUnit.DAYS.between(today, row.getJourneyDate()));
        return dto;
    }

    public List<JourneyStatusDto> toJourneyDtoList(List<FlightJourneySummary> rows, LocalDate today) {
        return rows.stream().map(r -> toJourneyDto(r, today)).toList();
    }

    /** FlightOverview row -> FlightOverviewDto. */
    public FlightOverviewDto toOverviewDto(FlightOverview row) {
        FlightOverviewDto dto = new FlightOverviewDto();
        dto.setFlightId(row.getFlightId());
        dto.setFlightNumber(row.getFlightNumber());
        dto.setSource(row.getSource());
        dto.setDestination(row.getDestination());
        dto.setDepartureTime(row.getDepartureTime());
        dto.setArrivalTime(row.getArrivalTime());
        dto.setCapacity(row.getCapacity());
        dto.setPricePerSeat(row.getPricePerSeat());
        dto.setUpcomingJourneys(row.getUpcomingJourneys());
        dto.setUpcomingPassengers(row.getUpcomingPassengers());
        dto.setCompletedJourneys(row.getCompletedJourneys());
        dto.setCompletedPassengers(row.getCompletedPassengers());
        return dto;
    }

    public List<FlightOverviewDto> toOverviewDtoList(List<FlightOverview> rows) {
        return rows.stream().map(this::toOverviewDto).toList();
    }
}