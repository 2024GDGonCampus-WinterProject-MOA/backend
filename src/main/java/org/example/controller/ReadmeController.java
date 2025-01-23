package org.example.controller;

import org.example.entity.ManagedRepository;
import org.example.repository.RepositoryManager;
import org.example.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReadmeController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private RepositoryManager repositoryManager;

    // README 생성 및 저장
    @PostMapping("/generate-readme")
    public String generateReadme(@RequestBody Map<String, Object> request) {
        try {
            var messages = (java.util.List<Map<String, String>>) request.get("messages");
            if (messages == null || messages.isEmpty()) {
                return "Error: Missing required parameter: 'messages'.";
            }

            var prompt = messages.get(0).get("content");
            if (prompt == null || prompt.isEmpty()) {
                return "Error: Missing 'content' in the 'messages' parameter.";
            }

            // OpenAI API 호출
            String generatedReadme = openAIService.generateReadme(prompt);

            // DB에 저장
            ManagedRepository repository = new ManagedRepository();
            repository.setReadme(generatedReadme);
            repository.setUpdatedAt(LocalDateTime.now()); // 최근 업데이트 시간 추가
            repositoryManager.save(repository);

            return "README 생성 및 저장 완료. Repository ID: " + repository.getId();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // README 수정
    @PutMapping("/update-readme/{id}")
    public String updateReadme(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String updatedReadme = request.get("readme");
            if (updatedReadme == null || updatedReadme.isEmpty()) {
                return "Error: 'readme' 내용이 비어 있습니다.";
            }

            // DB에서 데이터 찾기
            ManagedRepository repository = repositoryManager.findById(id)
                    .orElseThrow(() -> new RuntimeException("Repository not found with ID: " + id));

            // README 수정
            repository.setReadme(updatedReadme);
            repository.setUpdatedAt(LocalDateTime.now()); // 최근 업데이트 시간 갱신
            repositoryManager.save(repository);

            return "README 수정 완료. Repository ID: " + id;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // README 삭제
    @DeleteMapping("/delete-readme/{id}")
    public String deleteReadme(@PathVariable Long id) {
        try {
            // DB에서 데이터 찾기
            ManagedRepository repository = repositoryManager.findById(id)
                    .orElseThrow(() -> new RuntimeException("Repository not found with ID: " + id));

            // 삭제
            repositoryManager.delete(repository);

            return "README 삭제 완료. Repository ID: " + id;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}