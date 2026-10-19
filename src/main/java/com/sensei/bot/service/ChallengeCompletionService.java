package com.sensei.bot.service;

import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sensei.bot.model.ChallengeCompletion;
import com.sensei.bot.repository.ChallengeCompletionRepository;

/**
 * Temporary in-memory tracking for challenge completion while the database layer is disabled.
 *
 * <p>The original repository-based implementation remains in this class as dead code so it can be
 * re-enabled later without removing the historical logic. This fallback matches the same pattern
 * we used in the Discord bot, where a HashMap acts as a lightweight in-memory store.</p>
 */
@Service
public class ChallengeCompletionService {

    /**
     * Active fallback for now while JPA/DataSource auto-configuration is disabled.
     */
    private final Map<String, Boolean> solvedChallengeCache = new ConcurrentHashMap<>();

    /**
     * Dead code: original database-backed repository path retained for future restoration.
     */
    private final ChallengeCompletionRepository challengeCompletionRepository;

    @Autowired
    public ChallengeCompletionService(@Autowired(required = false) ChallengeCompletionRepository challengeCompletionRepository) {
        this.challengeCompletionRepository = challengeCompletionRepository;
    }

    /**
     * Stores a solved confirmation for the given guild, user, and challenge date.
     *
     * <p>When the database is disabled, we store the state in-memory. The repository-based logic
     * below is intentionally left in place as dormant code and can be restored later.</p>
     */
    @Transactional
    public void markUserSolved(String guildId, String userId, LocalDate challengeDate) {
        if (guildId == null || userId == null || challengeDate == null) {
            return;
        }

        String key = guildId + ":" + userId + ":" + challengeDate;

        // Temporary in-memory store is active while the database is disabled.
        solvedChallengeCache.put(key, true);

        // Dead code: original database-backed implementation kept for future reactivation.
        if (challengeCompletionRepository != null) {
            boolean exists = challengeCompletionRepository
                .findByGuildIdAndUserIdAndChallengeDate(guildId, userId, challengeDate)
                .isPresent();

            if (!exists) {
                challengeCompletionRepository.save(new ChallengeCompletion(guildId, userId, challengeDate));
            }
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

        String key = guildId + ":" + userId + ":" + challengeDate;
        if (solvedChallengeCache.containsKey(key)) {
            return true;
        }

        // Dead code: original database-backed implementation kept for future reactivation.
        if (challengeCompletionRepository != null) {
            return challengeCompletionRepository
                .findByGuildIdAndUserIdAndChallengeDate(guildId, userId, challengeDate)
                .isPresent();
        }

        return false;
    }
}
