package com.flightreservation.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PassengerForm {

    @NotBlank(message = "Passenger name is required.")
    @Size(max = 100, message = "Passenger name cannot exceed 100 characters.")
    private String passengerName;

    @NotBlank(message = "Please select the passenger's gender.")
    private String gender;

    @NotNull(message = "Passenger age is required.")
    @Min(value = 1, message = "Passenger age must be at least 1.")
    @Max(value = 120, message = "Passenger age cannot exceed 120.")
    private Integer age;

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}