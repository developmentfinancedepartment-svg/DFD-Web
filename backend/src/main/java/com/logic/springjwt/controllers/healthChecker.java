package com.logic.springjwt.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class healthChecker {

    @GetMapping("/api/test")
    public String testEndpoint() {
        return "Test endpoint is working!";
    }
}
