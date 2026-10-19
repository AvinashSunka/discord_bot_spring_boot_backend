package com.sensei.bot.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sensei.bot.model.ChallengeCompletion;

/**
 * Persistence layer for challenge completion records.
 *
 * <p>This repository lets the application quickly check whether a user already confirmed
 * solving today's daily challenge and store that record for future reminder logic.</p>
 */
public interface ChallengeCompletionRepository extends JpaRepository<ChallengeCompletion, Long> {

    /**
     * Looks up whether the user has already marked the challenge as solved for a specific date.
     */
    Optional<ChallengeCompletion> findByGuildIdAndUserIdAndChallengeDate(String guildId, String userId, LocalDate challengeDate);
}
