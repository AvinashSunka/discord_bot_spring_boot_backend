 package com.sensei.bot.controller;

import com.sensei.bot.service.LeetCodeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController
// public class LeetCodeController {

//     // Spring Boot automatically "injects" the service here (Dependency Injection)
//     private final LeetCodeService leetCodeService;

//     public LeetCodeController(LeetCodeService leetCodeService) {
//         this.leetCodeService = leetCodeService;
//     }

//     // produces = MediaType.APPLICATION_JSON_VALUE tells the browser to format it nicely
//     @GetMapping(value = "/api/leetcode", produces = MediaType.APPLICATION_JSON_VALUE)
//     public String getAllLeetCodeProblems() {
//         return leetCodeService.fetchAllProblems();
//     }
// }

import org.springframework.web.bind.annotation.PathVariable;
// ... your other imports

@RestController
public class LeetCodeController {

    private final LeetCodeService leetCodeService;

    public LeetCodeController(LeetCodeService leetCodeService) {
        this.leetCodeService = leetCodeService;
    }

    // ... your existing @GetMapping("/api/leetcode") ...

    // New endpoint to fetch by topic
    @GetMapping(value = "/api/leetcode/topics/{topic}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getProblemsByTopic(@PathVariable String topic) {
        // e.g., if user visits /api/leetcode/topics/array, topic = "array"
        return leetCodeService.fetchProblemsByTopic(topic);
    }
}