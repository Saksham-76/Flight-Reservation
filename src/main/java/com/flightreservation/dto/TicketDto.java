package com.flightreservation.dto;

import java.time.LocalDate;

public class TicketDto {
    private Long pnrNo;
    private String passengerName;
    private String gender;
    private int age;
    private LocalDate journeyDate;
    private String seatNo;
    private Long bookingId;
    private String status;
    private boolean cancellable;

    public Long getPnrNo() { return pnrNo; }
    public void setPnrNo(Long pnrNo) { this.pnrNo = pnrNo; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public LocalDate getJourneyDate() { return journeyDate; }
    public void setJourneyDate(LocalDate journeyDate) { this.journeyDate = journeyDate; }
    public String getSeatNo() { return seatNo; }
    public void setSeatNo(String seatNo) { this.seatNo = seatNo; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isCancellable() { return cancellable; }
    public void setCancellable(boolean cancellable) { this.cancellable = cancellable; }
}