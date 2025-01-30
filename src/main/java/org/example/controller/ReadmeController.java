package org.example.controller;

import org.example.entity.ManagedRepo;
import org.example.repository.RepoRepository;
import org.example.service.OpenAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ReadmeController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private RepoRepository repoRepository;

    // README 생성 및 저장
    @PostMapping("/generate-readme")
    public String generateReadme(@RequestBody Map<String, Object> request, Long Id) {
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
            Optional<ManagedRepo> optionalRepository = repoRepository.findById(Id);

            if (optionalRepository.isPresent()) {
                ManagedRepo repository = optionalRepository.get();
                repository.setMoa_readme(generatedReadme);
                if (repository.getHas_moa_readme() == 0) {
                    repository.setHas_moa_readme(1);
                }
                repoRepository.save(repository);
                return "README 생성 및 저장 완료. Repository ID: " + repository.getId();
            } else {
                return "Error: Repository not found with ID : " + Id + ".";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // README 읽기
    @GetMapping("/readme/{id}")
    public ResponseEntity<String> getReadmeById(@PathVariable Long id) {
        try {
            ManagedRepo repository = repoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("No repository found with id: " + id));

            if (repository.getReadme() == null || repository.getReadme().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("No readme file found for repository with id: " + id);
            }
            return ResponseEntity.ok(repository.getReadme());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // README 다운로드
    @GetMapping("/download-readme/{id}")
    public ResponseEntity<Resource> downloadReadme(@PathVariable Long id) {
        Optional<ManagedRepo> optionalRepository = repoRepository.findById(id);

        if (optionalRepository.isPresent()) {
            ManagedRepo repository = optionalRepository.get();
            String readmeContent = repository.getReadme();

            // .md 파일로 변환
            ByteArrayResource resource = new ByteArrayResource(readmeContent.getBytes(StandardCharsets.UTF_8));

            // HTTP 응답으로 파일 제공
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_MARKDOWN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"readme.md\"")
                    .body(resource);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
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
            ManagedRepo repository = repoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Repository not found with ID: " + id));

            // README 수정
            repository.setReadme(updatedReadme);
            repository.setUpdatedAt(LocalDateTime.now()); // 최근 업데이트 시간 갱신
            repoRepository.save(repository);

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
            ManagedRepo repository = repoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Repository not found with ID: " + id));

            // 삭제
            repository.setHas_moa_readme(0);
            repoRepository.delete(repository);

            return "README 삭제 완료. Repository ID: " + id;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}