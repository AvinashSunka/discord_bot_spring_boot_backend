package com.sensei.bot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LeetCodeService {

    private final RestTemplate restTemplate;
    private final String leetCodeGraphqlUrl;

    public LeetCodeService(RestTemplate restTemplate,
                           @Value("${leetcode.graphql.url:https://leetcode.com/graphql}") String leetCodeGraphqlUrl) {
        this.restTemplate = restTemplate;
        this.leetCodeGraphqlUrl = leetCodeGraphqlUrl;
    }

    public String fetchProblemsByTopic(String topicSlug) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

        String graphQlPayload = """
            {
                "query": "query problemsetQuestionList($categorySlug: String, $limit: Int, $skip: Int, $filters: QuestionListFilterInput) { problemsetQuestionList: questionList(categorySlug: $categorySlug, limit: $limit, skip: $skip, filters: $filters) { questions: data { frontendQuestionId: questionFrontendId title titleSlug difficulty topicTags { name slug } } } }",
                "variables": {
                    "categorySlug": "",
                    "skip": 0,
                    "limit": 50,
                    "filters": {
                        "tags": ["%s"]
                    }
                }
            }
            """.formatted(topicSlug);

        HttpEntity<String> request = new HttpEntity<>(graphQlPayload, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            leetCodeGraphqlUrl,
            request,
            String.class
        );

        return response.getBody();
    }
}