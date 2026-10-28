package com.flightreservation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DbTestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-test")
    @ResponseBody
    public String test() {
        Integer flights = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM flight", Integer.class);
        return "DB connected. Flights in table: " + flights;
    }
}