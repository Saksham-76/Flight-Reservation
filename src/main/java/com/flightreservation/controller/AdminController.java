package com.flightreservation.controller;

import com.flightreservation.dto.AddFlightForm;
import com.flightreservation.dto.FlightOverviewDto;
import com.flightreservation.dto.JourneyStatusDto;
import com.flightreservation.exception.DuplicateResourceException;
import com.flightreservation.service.FlightService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/flights")
public class AdminController {

    private final FlightService flightService;

    public AdminController(FlightService flightService) {
        this.flightService = flightService;
    }

    // ---------- Add Flight ----------

    @GetMapping("/add")
    public String showAddFlight(Model model) {
        model.addAttribute("addFlightForm", new AddFlightForm());
        return "admin/add-flight";
    }

    @PostMapping("/add")
    public String addFlight(@Valid @ModelAttribute("addFlightForm") AddFlightForm form,
                            BindingResult result, RedirectAttributes redirectAttrs) {

        if (!result.hasFieldErrors("source") && !result.hasFieldErrors("destination")
                && form.getSource().trim().equalsIgnoreCase(form.getDestination().trim())) {
            result.rejectValue("destination", "same.city",
                    "Source and destination cannot be the same");
        }
        if (result.hasErrors()) {
            return "admin/add-flight";
        }

        try {
            flightService.addFlight(form);
        } catch (DuplicateResourceException e) {
            result.rejectValue("flightNumber", "duplicate", e.getMessage());
            return "admin/add-flight";
        }

        redirectAttrs.addFlashAttribute("successMessage",
                "Flight " + form.getFlightNumber().trim().toUpperCase() + " added successfully.");
        return "redirect:/admin/flights/add";
    }

    // ---------- View Flights (Upcoming / Completed) ----------

    @GetMapping
    public String viewFlights(@RequestParam(defaultValue = "upcoming") String tab, Model model) {
        LocalDate today = LocalDate.now();
        List<JourneyStatusDto> upcoming = flightService.getUpcomingJourneys(today);
        List<JourneyStatusDto> completed = flightService.getCompletedJourneys(today);

        boolean showCompleted = "completed".equalsIgnoreCase(tab);
        model.addAttribute("today", today);
        model.addAttribute("tab", showCompleted ? "completed" : "upcoming");
        model.addAttribute("upcomingCount", upcoming.size());
        model.addAttribute("completedCount", completed.size());
        model.addAttribute("journeys", showCompleted ? completed : upcoming);
        return "admin/view-flights";
    }
    
    @GetMapping("/all")
    public String allFlights(@RequestParam(required = false) String q, Model model) {
        List<FlightOverviewDto> flights = flightService.getAllFlights(q, LocalDate.now());
        model.addAttribute("flights", flights);
        model.addAttribute("q", q == null ? "" : q.trim());
        model.addAttribute("today", LocalDate.now());
        return "admin/all-flights";
    }
}