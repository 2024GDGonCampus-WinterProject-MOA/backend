package org.example.controller;

import org.example.dto.RepositoryResponseDto;
import org.example.dto.RepositorySaveRequestDto;
import org.example.dto.RepositoryUpdateRequestDto;
import org.example.dto.SingleRepositoryResponseDto;
import org.example.service.GitHubService;
import org.example.service.RepoService;
import org.example.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repositories")
public class ApiController {

    private final RepoService repoService;
    private final GitHubService gitHubService;

    public ApiController(RepoService repoService, GitHubService gitHubService) {
        this.repoService = repoService;
        this.gitHubService = gitHubService;
    }

    // 1) 사용자 GitHub Repository 목록 가져오기
    @GetMapping("/list")
    public List<String> getUserRepositories() {
        String username = JwtUtil.getUsernameFromContext();
        if (username == null) {
            System.out.println("username이 없습니다.");
        }
        List<Map<String, Object>> repositories = gitHubService.fetchUserRepositories(username);

        return repositories.stream()
                .map(repo -> (String) repo.get("name"))
                .toList();
    }

    // 2) 선택된 Repository 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveSelectedRepository(@RequestBody RepositorySaveRequestDto requestDto) {
        String username = JwtUtil.getUsernameFromContext();
        if (username == null) {
            System.out.println("username이 없습니다.");
        }
        repoService.saveSelectedRepository(username, requestDto);

        return ResponseEntity.status(201).body("Repository saved");
    }

    // 3) 저장된 Repository 조회
    @GetMapping
    public ResponseEntity<List<RepositoryResponseDto>> getSavedRepositories() {
        String username = JwtUtil.getUsernameFromContext();
        if (username == null) {
            System.out.println("username이 없습니다.");
        }
        List<RepositoryResponseDto> repositories = repoService.getAllRepositories(username);
        return ResponseEntity.ok(repositories);
    }

    // 4) 저장된 Repository 단일 조회
    @GetMapping("{id}")
    public ResponseEntity<SingleRepositoryResponseDto> getSingleRepository(@PathVariable("id") Long id) {
        String username = JwtUtil.getUsernameFromContext();
        if (username == null) {
            System.out.println("username이 없습니다.");
        }
        SingleRepositoryResponseDto repository = repoService.getRepository(id);
        return ResponseEntity.ok(repository);
    }

    // 4) 저장된 Repository 수정
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRepository(@PathVariable Long id, @RequestBody RepositoryUpdateRequestDto requestDto) {
        repoService.updateRepository(id, requestDto);
        return ResponseEntity.ok("Repository updated");
    }

    // 5) 저장된 Repository 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRepository(@PathVariable Long id) {
        repoService.deleteRepository(id);
        return ResponseEntity.noContent().build();
    }

}
