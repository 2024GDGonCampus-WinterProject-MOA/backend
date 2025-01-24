package org.example.controller;

import org.example.dto.RepositoryResponseDto;
import org.example.dto.RepositorySaveRequestDto;
import org.example.dto.RepositoryUpdateRequestDto;
import org.example.service.GitHubService;
import org.example.service.RepositoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repositories")
public class ApiController {

    private final RepositoryService repositoryService;
    private final GitHubService gitHubService;

    public ApiController(RepositoryService repositoryService, GitHubService gitHubService) {
        this.repositoryService = repositoryService;
        this.gitHubService = gitHubService;
    }

    // 1) 사용자 GitHub Repository 목록 가져오기
    @GetMapping("/list")
    public ResponseEntity<List<String>> getUserRepositories(@AuthenticationPrincipal OAuth2User principal) {
        List<Map<String, Object>> repositories = gitHubService.fetchUserRepositories(principal);

        // 각 Repository 이름만 추출
        List<String> repositoryNames = repositories.stream()
                .map(repo -> (String) repo.get("name"))
                .toList();

        return ResponseEntity.ok(repositoryNames);
    }

    // 2) 선택된 Repository 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveSelectedRepository(@AuthenticationPrincipal OAuth2User principal, @RequestBody RepositorySaveRequestDto requestDto) {
        //OAuth2 인증된 사용자 정보에서 username 가져오기
        String username = principal.getAttribute("login");
        //서비스 계층으로 전달
        repositoryService.saveSelectedRepository(username, requestDto);

        return ResponseEntity.status(201).body("Repository saved");
    }

    // 3) 저장된 Repository 조회
    @GetMapping
    public ResponseEntity<List<RepositoryResponseDto>> getSavedRepositories(@RequestParam String username) {
        List<RepositoryResponseDto> repositories = repositoryService.getAllRepositories(username);
        return ResponseEntity.ok(repositories);
    }

    // 4) 저장된 Repository 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRepository(@PathVariable Long id, @RequestBody RepositoryUpdateRequestDto requestDto) {

        repositoryService.updateRepository(id, requestDto);

        return ResponseEntity.ok("Repository updated");
    }

    // 5) 저장된 Repository 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRepository(@PathVariable Long id) {
        repositoryService.deleteRepository(id);
        return ResponseEntity.noContent().build();
    }

}
