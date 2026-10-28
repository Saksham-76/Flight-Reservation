package com.flightreservation.controller;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingForm;
import com.flightreservation.dto.FlightDto;
import com.flightreservation.dto.PassengerForm;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.exception.ResourceNotFoundException;
import com.flightreservation.exception.SeatsUnavailableException;
import com.flightreservation.mapper.BookingMapper;
import com.flightreservation.service.BookingService;
import com.flightreservation.service.FlightService;
import com.flightreservation.util.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.LocalDate;

@Controller
@RequestMapping("/book")
public class BookingController {

    private final FlightService flightService;
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    public BookingController(FlightService flightService, BookingService bookingService,
                             BookingMapper bookingMapper) {
        this.flightService = flightService;
        this.bookingService = bookingService;
        this.bookingMapper = bookingMapper;
    }

    // ---------- Step 1: Booking Form ----------

    @GetMapping("/{flightNumber}")
    public String showBookingForm(@PathVariable String flightNumber, Model model) {
        model.addAttribute("flight", findFlight(flightNumber));
        model.addAttribute("bookingForm", new BookingForm());
        model.addAttribute("today", LocalDate.now());
        return "booking/booking-form";
    }

    @PostMapping("/{flightNumber}")
    public String submitBookingForm(@PathVariable String flightNumber,
                                    @Valid @ModelAttribute("bookingForm") BookingForm bookingForm,
                                    BindingResult result, HttpSession session, Model model) {
        FlightDto flight = findFlight(flightNumber);
        model.addAttribute("flight", flight);
        model.addAttribute("today", LocalDate.now());

        if (result.hasErrors()) {
            return "booking/booking-form";
        }

        int available = bookingService.getAvailableSeats(
                flight.getFlightId(), flight.getCapacity(), bookingForm.getJourneyDate());
        if (available < bookingForm.getNumberOfPassengers()) {
            model.addAttribute("seatError", "Booking not allowed: only " + available
                    + " seat(s) available on " + bookingForm.getJourneyDate()
                    + ", but you requested " + bookingForm.getNumberOfPassengers() + ".");
            return "booking/booking-form";
        }

        session.setAttribute(AppConstants.BOOKING_DRAFT, bookingMapper.toDraft(flight, bookingForm));
        return "redirect:/book/passenger";
    }

    // ---------- Step 2: Add Passenger (repeats once per passenger) ----------

    @GetMapping("/passenger")
    public String showPassengerForm(HttpSession session, Model model) {
        BookingDraft draft = getDraft(session);
        if (draft == null) {
            return "redirect:/";
        }
        model.addAttribute("passengerForm", new PassengerForm());
        addPassengerStepInfo(model, draft);
        return "booking/add-passenger";
    }

    @PostMapping("/passenger")
    public String addPassenger(@Valid @ModelAttribute("passengerForm") PassengerForm passengerForm,
                               BindingResult result, HttpSession session, Model model,
                               RedirectAttributes redirectAttrs) {
        BookingDraft draft = getDraft(session);
        if (draft == null) {
            return "redirect:/";
        }
        if (result.hasErrors()) {
            addPassengerStepInfo(model, draft);
            return "booking/add-passenger";
        }

        draft.getPassengers().add(passengerForm);

        if (draft.getPassengers().size() < draft.getNumberOfPassengers()) {
            return "redirect:/book/passenger";            // next passenger
        }

        // Last passenger: complete the booking
        SessionUser user = (SessionUser) session.getAttribute(AppConstants.SESSION_USER);
        try {
            long bookingId = bookingService.createBooking(draft, user.getUserId());
            session.removeAttribute(AppConstants.BOOKING_DRAFT);
            return "redirect:/book/confirmation/" + bookingId;
        } catch (SeatsUnavailableException e) {
            // someone else booked the seats while this user was filling in passengers
            session.removeAttribute(AppConstants.BOOKING_DRAFT);
            redirectAttrs.addFlashAttribute("seatError", e.getMessage());
            return "redirect:/book/" + draft.getFlightNumber();
        }
    }

    // ---------- Step 3: Booking Confirmation ----------

    @GetMapping("/confirmation/{bookingId}")
    public String confirmation(@PathVariable long bookingId, HttpSession session, Model model) {
        SessionUser user = (SessionUser) session.getAttribute(AppConstants.SESSION_USER);
        model.addAttribute("booking", bookingService.getBooking(bookingId, user.getUserId()));
        return "booking/booking-confirmation";
    }

    // ---------- helpers ----------

    private FlightDto findFlight(String flightNumber) {
        return flightService.findByFlightNumber(flightNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Flight " + flightNumber + " not found"));
    }

    private BookingDraft getDraft(HttpSession session) {
        return (BookingDraft) session.getAttribute(AppConstants.BOOKING_DRAFT);
    }

    private void addPassengerStepInfo(Model model, BookingDraft draft) {
        int total = draft.getNumberOfPassengers();
        int added = draft.getPassengers().size();
        int current = added + 1;
        model.addAttribute("currentPassenger", current);
        model.addAttribute("totalPassengers", total);
        model.addAttribute("remaining", total - added);
        model.addAttribute("progress", current * 100 / total);
        model.addAttribute("lastPassenger", current == total);
    }
}