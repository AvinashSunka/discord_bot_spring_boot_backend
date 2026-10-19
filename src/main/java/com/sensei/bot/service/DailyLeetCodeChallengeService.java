package com.sensei.bot.service;

import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DailyLeetCodeChallengeService {

    private static final Logger logger = LoggerFactory.getLogger(DailyLeetCodeChallengeService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${daily.challenge.wrapper-url:https://leetcode-wrapper.onrender.com/api/daily}")
    private String leetcodeWrapperUrl;

    @Value("${daily.challenge.discord.bot-url:}")
    private String discordBotUrl;

    @Value("${daily.challenge.discord.channel-id:}")
    private String discordChannelId;

    @Scheduled(cron = "0 0 0 * * *")
    public void postDailyChallenge() {
        try {
            Map<String, Object> challenge = fetchTodayChallenge();
            if (challenge == null || challenge.isEmpty()) {
                logger.warn("No daily challenge available for today.");
                return;
            }

            String title = String.valueOf(challenge.getOrDefault("title", "Daily LeetCode Challenge"));
            String slug = String.valueOf(challenge.getOrDefault("titleSlug", ""));
            String difficulty = String.valueOf(challenge.getOrDefault("difficulty", "Unknown"));
            Object frontendQuestionId = challenge.get("frontendQuestionId");
            String date = LocalDate.now().toString();

            sendToDiscord(title, slug, difficulty, frontendQuestionId, date);
        } catch (Exception e) {
            logger.error("Failed to post daily LeetCode challenge", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchTodayChallenge() {
        ResponseEntity<Map> response = restTemplate.getForEntity(leetcodeWrapperUrl, Map.class);
        Map<String, Object> body = response.getBody();

        if (body == null) {
            return null;
        }

        if (body.containsKey("data") && body.get("data") instanceof Map<?, ?> dataMap) {
            Object maybeQuestion = ((Map<?, ?>) dataMap).get("question");
            if (maybeQuestion instanceof Map<?, ?> questionMap) {
                return (Map<String, Object>) questionMap;
            }
            return (Map<String, Object>) dataMap;
        }

        if (body.containsKey("question") && body.get("question") instanceof Map<?, ?> questionMap) {
            return (Map<String, Object>) questionMap;
        }

        return body;
    }

    private void sendToDiscord(String title, String slug, String difficulty, Object frontendQuestionId, String date) {
        if (discordBotUrl == null || discordBotUrl.isBlank()) {
            logger.warn("Discord bot URL is not configured. Daily challenge not sent.");
            return;
        }

        if (discordChannelId == null || discordChannelId.isBlank()) {
            logger.warn("Discord channel ID is not configured. Daily challenge not sent.");
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = Map.of(
                "channelId", discordChannelId,
                "title", title,
                "titleSlug", slug,
                "difficulty", difficulty,
                "questionFrontendId", frontendQuestionId != null ? frontendQuestionId : "",
                "date", date
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        String url = discordBotUrl.endsWith("/") ? discordBotUrl + "internal/daily-challenge" : discordBotUrl + "/internal/daily-challenge";

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            logger.warn("Discord bot returned status {}: {}", response.getStatusCode(), response.getBody());
        }
    }
}
