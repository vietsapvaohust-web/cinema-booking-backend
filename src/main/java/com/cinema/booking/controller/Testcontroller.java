package com.cinema.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testcontroller {

    @GetMapping("/api/ping")
    public String ping() {
        return "pong - server is running!";
    }
}