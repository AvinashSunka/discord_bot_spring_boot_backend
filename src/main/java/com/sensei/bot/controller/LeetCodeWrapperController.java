package com.sensei.bot.controller;

import org.springframework.web.bind.annotation.RestController;

import com.sensei.bot.service.LeetCodeWrapperService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class LeetCodeWrapperController {
    private final LeetCodeWrapperService leetCodeWrapperService;

    public LeetCodeWrapperController(LeetCodeWrapperService leetCodeWrapperService) {
        this.leetCodeWrapperService = leetCodeWrapperService;
    }

    // To fetch data from the LeetCode wrapper service based on the provided pattern
    @GetMapping(value = "/leetcode/wrapper", produces = "application/json")
    public String getMethodName(@RequestParam String patternName) {
        return leetCodeWrapperService.fetchData(patternName);
    }
    
}