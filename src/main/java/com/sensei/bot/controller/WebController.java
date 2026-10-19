package com.sensei.bot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/home")
    public String showHomePage() {
        return "home"; 
    }

    // Add this new route
    @GetMapping("/problems")
    public String showProblemsPage() {
        return "problems"; 
    }
}