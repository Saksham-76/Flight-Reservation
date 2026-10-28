package com.flightreservation.controller;

import com.flightreservation.dto.SearchForm;
import com.flightreservation.dto.FlightDto;
import com.flightreservation.service.FlightService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class HomeController {

    private final FlightService flightService;

    public HomeController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "home";
    }

    @PostMapping("/search")
    public String search(@Valid @ModelAttribute("searchForm") SearchForm searchForm,
                         BindingResult result, Model model) {

        if (!result.hasErrors() && searchForm.getSource().trim()
                .equalsIgnoreCase(searchForm.getDestination().trim())) {
            result.rejectValue("destination", "same.city",
                    "Source and destination cannot be the same");
        }
        if (result.hasErrors()) {
            return "home";
        }

        List<FlightDto> flights = flightService.searchFlights(
                searchForm.getSource(), searchForm.getDestination());

        model.addAttribute("searched", true);
        model.addAttribute("flights", flights);
        return "home";
    }
}