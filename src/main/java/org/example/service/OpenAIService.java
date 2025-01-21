package org.example.service;

import org.example.dto.OpenAIRequest;
import org.example.dto.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    public String generateReadme(String projectDetails) {
        try {
            // 요청 메시지 생성
            OpenAIRequest.Message userMessage = new OpenAIRequest.Message("user", projectDetails);
            OpenAIRequest request = new OpenAIRequest("gpt-3.5-turbo", Collections.singletonList(userMessage));

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Content-Type", "application/json");

            // HTTP 요청 보내기
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<OpenAIRequest> httpEntity = new HttpEntity<>(request, headers);
            ResponseEntity<OpenAIResponse> response = restTemplate.postForEntity(OPENAI_URL, httpEntity, OpenAIResponse.class);

            // 결과 반환
            OpenAIResponse openAIResponse = response.getBody();
            if (openAIResponse != null && openAIResponse.getChoices() != null && !openAIResponse.getChoices().isEmpty()) {
                return openAIResponse.getChoices().get(0).getMessage().getContent();
            } else {
                return "Error: No response from OpenAI.";
            }

        } catch (Exception e) {
            // 에러 처리
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}