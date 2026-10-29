package com.flightreservation.controller;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.flightreservation.dto.FlightDto;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.service.FlightService;
import com.flightreservation.util.AppConstants;

@Controller
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    private String checkAdminAccess(HttpSession session) {

        SessionUser user = (SessionUser) session.getAttribute(
                AppConstants.SESSION_USER);

        if (user == null) {
            return "redirect:/login";
        }

        if (!AppConstants.ROLE_ADMIN.equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return null;
    }


    
}