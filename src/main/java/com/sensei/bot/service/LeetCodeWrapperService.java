package com.sensei.bot.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class LeetCodeWrapperService {

    private static final String WRAPPER_API_URL = "https://leetcode-wrapper.onrender.com/api/problems";

    public String fetchData(String patternName) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("pattern", patternName);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    WRAPPER_API_URL,
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
