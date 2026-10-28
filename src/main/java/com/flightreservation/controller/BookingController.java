package com.flightreservation.controller;

import com.flightreservation.dto.BookingDraft;
import com.flightreservation.dto.BookingForm;
import com.flightreservation.dto.FlightDto;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.service.FlightService;
import com.flightreservation.util.AppConstants;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.servlet.http.HttpSession;

@Controller
public class BookingController {

    private final FlightService flightService;

    public BookingController(FlightService flightService) {
        this.flightService = flightService;
    }

    /**
     * Displays the booking form for the selected flight.
     */
    @GetMapping("/booking")
    public String showBookingForm(
            @RequestParam("flightId") Long flightId,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        SessionUser user =
                (SessionUser) session.getAttribute(AppConstants.SESSION_USER);

        if (user == null) {
            session.setAttribute(
                    AppConstants.REDIRECT_AFTER_LOGIN,
                    "/booking?flightId=" + flightId
            );
            return "redirect:/login";
        }

        FlightDto flight = flightService.getFlightById(flightId);

        if (flight == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The selected flight could not be found.");
            return "redirect:/";
        }

        model.addAttribute("flight", flight);
        model.addAttribute("bookingForm", new BookingForm());

        return "booking";
    }

    /**
     * Validates the journey date and passenger count,
     * then saves the booking draft in the session.
     */
    @PostMapping("/booking")
    public String submitBookingForm(
            @RequestParam("flightId") Long flightId,
            @Valid @ModelAttribute("bookingForm") BookingForm bookingForm,
            BindingResult bindingResult,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        SessionUser user =
                (SessionUser) session.getAttribute(AppConstants.SESSION_USER);

        if (user == null) {
            session.setAttribute(
                    AppConstants.REDIRECT_AFTER_LOGIN,
                    "/booking?flightId=" + flightId
            );
            return "redirect:/login";
        }

        FlightDto flight = flightService.getFlightById(flightId);

        if (flight == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "The selected flight could not be found.");
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("flight", flight);
            return "booking";
        }

        BookingDraft draft = new BookingDraft();
        draft.setFlightId(flight.getFlightId());
        draft.setFlightNumber(flight.getFlightNumber());
        draft.setSource(flight.getSource());
        draft.setDestination(flight.getDestination());
        draft.setJourneyDate(bookingForm.getJourneyDate());
        draft.setPassengerCount(bookingForm.getPassengerCount());

        session.setAttribute("bookingDraft", draft);

        return "redirect:/booking/passengers";
    }
}