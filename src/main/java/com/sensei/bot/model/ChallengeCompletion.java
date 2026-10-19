package com.sensei.bot.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Represents a single confirmation that a Discord user solved the daily LeetCode challenge
 * for a specific guild and date.
 *
 * <p>The combination of guildId + userId + challengeDate is unique, which prevents a user
 * from marking the same daily challenge as solved multiple times.</p>
 */
@Entity
@Table(
    name = "challenge_completion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"guild_id", "user_id", "challenge_date"})
)
public class ChallengeCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guild_id", nullable = false)
    private String guildId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "challenge_date", nullable = false)
    private LocalDate challengeDate;

    protected ChallengeCompletion() {
    }

    public ChallengeCompletion(String guildId, String userId, LocalDate challengeDate) {
        this.guildId = guildId;
        this.userId = userId;
        this.challengeDate = challengeDate;
    }

    public Long getId() {
        return id;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDate getChallengeDate() {
        return challengeDate;
    }
}
