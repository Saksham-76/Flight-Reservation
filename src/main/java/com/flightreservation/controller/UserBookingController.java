package com.flightreservation.controller;

import com.flightreservation.dto.SessionUser;
import com.flightreservation.exception.BookingNotCancellableException;
import com.flightreservation.service.BookingService;
import com.flightreservation.util.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/bookings")
public class UserBookingController {

    private final BookingService bookingService;

    public UserBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // ---------- My Bookings ----------

    @GetMapping
    public String myBookings(HttpSession session, Model model) {
        model.addAttribute("bookings", bookingService.getBookingsForUser(currentUserId(session)));
        return "user/my-bookings";
    }

    @PostMapping("/{bookingId}/cancel")
    public String cancelBooking(@PathVariable long bookingId, HttpSession session,
                                RedirectAttributes redirectAttrs) {
        try {
            bookingService.cancelBooking(bookingId, currentUserId(session));
            redirectAttrs.addFlashAttribute("successMessage",
                    "Booking #" + bookingId + " cancelled. Its seats have been released.");
        } catch (BookingNotCancellableException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/bookings";
    }

    // ---------- View Tickets ----------

    @GetMapping("/{bookingId}/tickets")
    public String viewTickets(@PathVariable long bookingId, HttpSession session, Model model) {
        long userId = currentUserId(session);
        model.addAttribute("booking", bookingService.getBooking(bookingId, userId));
        model.addAttribute("tickets", bookingService.getTickets(bookingId, userId));
        return "user/tickets";
    }

    @PostMapping("/{bookingId}/tickets/{pnrNo}/cancel")
    public String cancelTicket(@PathVariable long bookingId, @PathVariable long pnrNo,
                               HttpSession session, RedirectAttributes redirectAttrs) {
        try {
            bookingService.cancelTicket(bookingId, pnrNo, currentUserId(session));
            redirectAttrs.addFlashAttribute("successMessage",
                    "Ticket PNR " + pnrNo + " cancelled. The seat has been released.");
        } catch (BookingNotCancellableException e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/bookings/" + bookingId + "/tickets";
    }

    private long currentUserId(HttpSession session) {
        return ((SessionUser) session.getAttribute(AppConstants.SESSION_USER)).getUserId();
    }
}