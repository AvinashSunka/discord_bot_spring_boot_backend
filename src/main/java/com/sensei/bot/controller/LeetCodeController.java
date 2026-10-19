package com.sensei.bot.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sensei.bot.service.LeetCodeService;

@RestController
@RequestMapping("/api/leetcode")
public class LeetCodeController {

    private final LeetCodeService leetCodeService;

    public LeetCodeController(LeetCodeService leetCodeService) {
        this.leetCodeService = leetCodeService;
    }

    @GetMapping(value = "/topics/{topic}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getProblemsByTopic(@PathVariable String topic) {
        return leetCodeService.fetchProblemsByTopic(topic);
    }
}
