package com.sensei.bot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
@RequestMapping("/api")
public class SimpleController {
    @GetMapping("/leetcode")
    public String sayHello() {
        return "Hello from the Bot Controller!";
    }
}

