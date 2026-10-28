package com.flightreservation.controller;

import com.flightreservation.dto.LoginForm;
import com.flightreservation.dto.SessionUser;
import com.flightreservation.dto.SignupForm;
import com.flightreservation.exception.DuplicateResourceException;
import com.flightreservation.exception.InvalidCredentialsException;
import com.flightreservation.service.UserService;
import com.flightreservation.util.AppConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ---------------- Sign up ----------------

    @GetMapping("/signup")
    public String showSignup(Model model, HttpSession session) {
        if (session.getAttribute(AppConstants.SESSION_USER) != null) {
            return "redirect:/";
        }
        model.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("signupForm") SignupForm signupForm,
                         BindingResult result, RedirectAttributes redirectAttrs) {
        if (result.hasErrors()) {
            return "signup";
        }
        try {
            userService.register(signupForm);
        } catch (DuplicateResourceException e) {
            result.rejectValue("email", "duplicate", e.getMessage());
            return "signup";
        }
        redirectAttrs.addFlashAttribute("successMessage",
                "Account created successfully. Please log in.");
        return "redirect:/login";
    }

    // ---------------- Login / Logout ----------------

    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        if (session.getAttribute(AppConstants.SESSION_USER) != null) {
            return "redirect:/";
        }
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("loginForm") LoginForm loginForm,
                        BindingResult result, HttpServletRequest request, Model model) {
        if (result.hasErrors()) {
            return "login";
        }

        SessionUser user;
        try {
            user = userService.login(loginForm);
        } catch (InvalidCredentialsException e) {
            model.addAttribute("loginError", e.getMessage());
            return "login";
        }

        HttpSession session = request.getSession();
        request.changeSessionId();   // new session id after login prevents session fixation
        session.setAttribute(AppConstants.SESSION_USER, user);

        // If the user was sent to login from a protected page (e.g. Book), go back there
        String target = (String) session.getAttribute(AppConstants.REDIRECT_AFTER_LOGIN);
        session.removeAttribute(AppConstants.REDIRECT_AFTER_LOGIN);
        if (target != null) {
            return "redirect:" + target;
        }
        return AppConstants.ROLE_ADMIN.equals(user.getRole())
                ? "redirect:/admin/dashboard"
                : "redirect:/user/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}