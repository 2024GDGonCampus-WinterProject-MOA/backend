package org.example.controller;

import org.example.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReadmeController {

    @Autowired
    private OpenAIService openAIService;

    @PostMapping("/generate-readme")
    public String generateReadme(@RequestBody Map<String, Object> request) {
        try {
            // "messages" 필드를 확인
            var messages = (java.util.List<Map<String, String>>) request.get("messages");
            if (messages == null || messages.isEmpty()) {
                return "Error: Missing required parameter: 'messages'.";
            }

            // 첫 번째 메시지의 'content' 확인
            var prompt = messages.get(0).get("content");
            if (prompt == null || prompt.isEmpty()) {
                return "Error: Missing 'content' in the 'messages' parameter.";
            }

            // OpenAIService 호출
            return openAIService.generateReadme(prompt);

        } catch (Exception e) {
            // 예외 처리
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}