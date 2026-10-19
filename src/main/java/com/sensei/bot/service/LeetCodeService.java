// package com.sensei.bot.service;

// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// @Service
// public class LeetCodeService {

//     private final String LEETCODE_API_URL = "https://leetcode.com/api/problems/all/";

//     public String fetchAllProblems() {
//         RestTemplate restTemplate = new RestTemplate();
        
//         // 1. Set up headers to bypass basic bot-blocking
//         HttpHeaders headers = new HttpHeaders();
//         headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        
//         // 2. Wrap headers in an HTTP Entity
//         HttpEntity<String> entity = new HttpEntity<>(headers);
        
//         // 3. Make the GET request to LeetCode
//         ResponseEntity<String> response = restTemplate.exchange(
//             LEETCODE_API_URL, 
//             HttpMethod.GET, 
//             entity, 
//             String.class
//         );
        
//         // 4. Return the massive raw JSON string
//         return response.getBody();
//     }
// }


package com.sensei.bot.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LeetCodeService {

    // ... your existing fetchAllProblems() method ...

    public String fetchProblemsByTopic(String topicSlug) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

        // Java 21 Text Block containing the GraphQL payload
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
        
        // Notice this is a POST request to the /graphql endpoint
        ResponseEntity<String> response = restTemplate.postForEntity(
            "https://leetcode.com/graphql", 
            request, 
            String.class
        );
        
        return response.getBody();
    }
}