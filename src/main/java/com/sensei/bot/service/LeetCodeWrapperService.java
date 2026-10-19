package com.sensei.bot.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class LeetCodeWrapperService {

    private final RestTemplate restTemplate;
    private final String wrapperApiUrl;

    public LeetCodeWrapperService(RestTemplate restTemplate,
                                 @Value("${leetcode.wrapper.url:https://leetcode-wrapper.onrender.com/api/problems}") String wrapperApiUrl) {
        this.restTemplate = restTemplate;
        this.wrapperApiUrl = wrapperApiUrl;
    }

    public String fetchData(String patternName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of("pattern", patternName);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                wrapperApiUrl,
                request,
                String.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            if (responseBody != null && !responseBody.isBlank()) {
                return responseBody;
            }

            return "{\"success\":false,\"message\":\"Pattern name '" + patternName + "' is wrong or not found on LeetCode.\",\"pattern\":\"" + patternName + "\",\"count\":0,\"data\":[]}";
        }
    }
}
