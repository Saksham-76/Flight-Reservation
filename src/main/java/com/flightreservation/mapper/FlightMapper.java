package com.flightreservation.mapper;

import com.flightreservation.dto.FlightDto;
import com.flightreservation.model.Flight;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightMapper {

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
}