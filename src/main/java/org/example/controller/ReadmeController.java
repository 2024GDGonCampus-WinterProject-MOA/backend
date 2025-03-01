package org.example.controller;

import org.example.entity.ManagedRepo;
import org.example.repository.RepoRepository;
import org.example.service.GitHubService;
import org.example.service.OpenAIService;
import org.example.utils.JwtUtil;
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
    private GitHubService gitHubService;

    @Autowired
    private RepoRepository repoRepository;



    // README 생성 및 저장
    @PostMapping("/generate-readme/{id}")
    public String generateReadme(@RequestBody Map<String, Object> request, Long Id) {
        try {
            var messages = (java.util.List<Map<String, String>>) request.get("messages");
            if (messages == null || messages.isEmpty()) {
                return "Error: Missing required parameter: 'messages'.";
            }

            // 모아 프롬프트
            var moa_prompt = """
                    [prompt start]
                    같이 제공될 발표 대본의 내용을 바탕으로 프로젝트의 핵심 정보를 파악한 뒤,
                    다음의 내용을 필히 포함하여 README.md 파일을 작성하라.
                    마크다운 코드로 제공하라.
                    - 프로젝트 제목
                    - 프로젝트 로고 또는 대표 이미지 (너비 300px, 가운데 정렬)
                    - Repository 방문 횟수 (선택사항)
                    - 프로젝트 소개 및 개발 기간
                    - 배포 주소 (있는 경우)
                    - 팀 소개 (팀 프로젝트인 경우)
                    - 시작 가이드 (요구사항, 설치 및 실행 방법)
                    - 기술 스택 (가능하면 배지 사용)
                    - 화면 구성 또는 API 주소
                    - 주요 기능
                    - 아키텍처 및 디렉토리 구조
                    - 기타 추가 사항 (개발 문서, 회고 등)
                    
                    다음에 유의하라.
                    - 필요에 따라 언급되지 않은 내용을 추가/수정/삭제하라. 이 과정에서 창작을 해도 좋다.
                    - 코드 블록, 표를 포함하여 가시적으로 표시할 수 있는 수단을 적극 활용하라.
                    - 전문적이고 간결한 어조를 유지하라.
                    - 코드 이외의 다른 메시지는 일체 작성하지 않는다.
                    - 내용은 사용자가 수정할 필요가 없도록 완벽해야한다. 사용자는 내용을 일체 수정 없이 바로 사용한다.
                    [prompt end]
                    [script start]
                    """;

            var script = messages.get(0).get("content");

            if (script == null || script.isEmpty()) {
                return "Error: Missing 'content' in the 'messages' parameter.";
            }

            var prompt = moa_prompt + script;

            // OpenAI API 호출
            String generatedReadme = openAIService.generateReadme(prompt);

            // DB에 저장
            Optional<ManagedRepo> optionalRepository = repoRepository.findById(Id);

            if (optionalRepository.isPresent()) {
                ManagedRepo repository = optionalRepository.get();
                repository.setMoa_readme(generatedReadme);
                if (repository.getHas_moa_readme() == 0 || repository.getHas_moa_readme() == null) {
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

    // README 다운로드
    @GetMapping("/download-readme/original/{owner}/{reponame}")
    public String downloadReadmeOriginal(@PathVariable String owner, @PathVariable String reponame) {
        String username = JwtUtil.getUsernameFromContext();
        return gitHubService.fetchReadme(owner, reponame, username);
    }

    // README 수정
    @PutMapping("/update-readme/{id}")
    public String updateReadme(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String updatedReadme = request.get("readme");
            if (updatedReadme == null || updatedReadme.isEmpty()) {
                return "Error: 'readme' 내용이 비어 있습니다.";
            }

            // DB에서 레포지토리 찾기
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

    // README 삭제 (README 칸 비우기)
    @PutMapping("/delete-readme/{id}")
    public String clearReadme(@PathVariable Long id) {
        try {
            // DB에서 레포지토리 찾기
            ManagedRepo repository = repoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Repository not found with ID: " + id));

            // readme 삭제
            repository.setHas_moa_readme(0);
            repository.setReadme("");
            repository.setUpdatedAt(LocalDateTime.now());
            repoRepository.save(repository);

            return "README가 초기화 완료. Repository ID: " + id;

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}