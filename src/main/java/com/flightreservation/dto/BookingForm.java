package com.flightreservation.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.FutureOrPresent;

public class BookingForm {

    @NotNull(message = "Please select a journey date.")
    @FutureOrPresent(message = "Journey date cannot be in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate journeyDate;

    @NotNull(message = "Please enter the number of passengers.")
    @Min(value = 1, message = "At least one passenger is required.")
    @Max(value = 10, message = "Maximum 10 passengers are allowed.")
    private Integer passengerCount = 1;

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public Integer getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(Integer passengerCount) {
        this.passengerCount = passengerCount;
    }
}