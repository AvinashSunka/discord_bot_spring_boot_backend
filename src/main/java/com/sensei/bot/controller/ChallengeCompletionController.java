package com.sensei.bot.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sensei.bot.service.ChallengeCompletionService;

/**
 * Exposes the daily challenge completion API to the Discord bot and any other service
 * that needs to read or write solved-state information.
 *
 * <p>The bot uses this controller to send solved confirmations and to check whether a user
 * should still receive reminders for the active daily challenge.</p>
 */
@RestController
@RequestMapping("/api")
public class ChallengeCompletionController {

    private final ChallengeCompletionService challengeCompletionService;

    public ChallengeCompletionController(ChallengeCompletionService challengeCompletionService) {
        this.challengeCompletionService = challengeCompletionService;
    }

    /**
     * Persists the fact that a guild member has solved the challenge for the given date.
     *
     * <p>This endpoint is used by the Discord bot after a user confirms completion.</p>
     */
    @PostMapping("/challenge-completion")
    public ResponseEntity<String> markSolved(
            @RequestParam String guildId,
            @RequestParam String userId,
            @RequestParam String challengeDate) {

        challengeCompletionService.markUserSolved(guildId, userId, LocalDate.parse(challengeDate));
        return ResponseEntity.ok("Saved");
    }

    /**
     * Returns whether that user has already marked the daily challenge as solved for that date.
     *
     * <p>The reminder service uses this check to avoid spamming users who have already
     * completed the challenge.</p>
     */
    @GetMapping("/challenge-completion")
    public ResponseEntity<Boolean> hasSolved(
            @RequestParam String guildId,
            @RequestParam String userId,
            @RequestParam String challengeDate) {

        boolean solved = challengeCompletionService.hasUserSolvedToday(guildId, userId, LocalDate.parse(challengeDate));
        return ResponseEntity.ok(solved);
    }
}
