package com.sensei.bot.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sensei.bot.model.ChallengeCompletion;
import com.sensei.bot.repository.ChallengeCompletionRepository;

/**
 * Application service responsible for tracking challenge completion status.
 *
 * <p>The bot uses this service to record when a user confirms they solved the daily challenge.
 * Reminder logic can then query the database to decide whether to keep sending DMs.</p>
 */
@Service
public class ChallengeCompletionService {

    private final ChallengeCompletionRepository challengeCompletionRepository;

    public ChallengeCompletionService(ChallengeCompletionRepository challengeCompletionRepository) {
        this.challengeCompletionRepository = challengeCompletionRepository;
    }

    /**
     * Stores a solved confirmation for the given guild, user, and challenge date.
     *
     * <p>If a record already exists, we do nothing to avoid duplicates.</p>
     */
    @Transactional
    public void markUserSolved(String guildId, String userId, LocalDate challengeDate) {
        if (guildId == null || userId == null || challengeDate == null) {
            return;
        }

        boolean exists = challengeCompletionRepository
            .findByGuildIdAndUserIdAndChallengeDate(guildId, userId, challengeDate)
            .isPresent();

        if (!exists) {
            challengeCompletionRepository.save(new ChallengeCompletion(guildId, userId, challengeDate));
        }
    }

    /**
     * Returns true when the user has already confirmed that they solved this challenge
     * on the provided date.
     */
    public boolean hasUserSolvedToday(String guildId, String userId, LocalDate challengeDate) {
        if (guildId == null || userId == null || challengeDate == null) {
            return false;
        }

        return challengeCompletionRepository
            .findByGuildIdAndUserIdAndChallengeDate(guildId, userId, challengeDate)
            .isPresent();
    }
}
