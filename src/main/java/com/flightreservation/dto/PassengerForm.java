package com.flightreservation.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class PassengerForm implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "Passenger name is required")
    @Size(max = 50, message = "Name can be at most 50 characters")
    private String passengerName;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be between 0 and 120")
    @Max(value = 120, message = "Age must be between 0 and 120")
    private Integer age;

    @NotBlank(message = "Please select a gender")
    @Pattern(regexp = "Male|Female|Other", message = "Please select a valid gender")
    private String gender;

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}