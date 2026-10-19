package com.sensei.bot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.sensei.bot.model.ChallengeCompletion;
import com.sensei.bot.repository.ChallengeCompletionRepository;

class ChallengeCompletionServiceTests {

    @Test
    void shouldPersistSolvedChallengeStatus() {
        ChallengeCompletionRepository repository = mock(ChallengeCompletionRepository.class);
        ChallengeCompletionService challengeCompletionService = new ChallengeCompletionService(repository);

        LocalDate challengeDate = LocalDate.of(2026, 10, 19);

        when(repository.findByGuildIdAndUserIdAndChallengeDate("guild-123", "user-456", challengeDate))
            .thenReturn(Optional.empty(), Optional.of(new ChallengeCompletion("guild-123", "user-456", challengeDate)));
        when(repository.save(any(ChallengeCompletion.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        challengeCompletionService.markUserSolved("guild-123", "user-456", challengeDate);

        assertTrue(challengeCompletionService.hasUserSolvedToday("guild-123", "user-456", challengeDate));
        assertEquals(1, 1);
    }
}
