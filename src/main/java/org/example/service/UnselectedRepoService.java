package org.example.service;

import org.example.entity.ManagedRepo;
import org.example.repository.RepoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UnselectedRepoService {

    private final RepoRepository repoRepository;
    private final GitHubService gitHubService;

    public UnselectedRepoService(RepoRepository repoRepository, GitHubService gitHubService) {
        this.repoRepository = repoRepository;
        this.gitHubService = gitHubService;
    }

    public List<Map<String, Object>> getUnselectedRepositories(String username) {
        // 1. GitHub에서 모든 레포지토리 가져오기
        List<Map<String, Object>> allReposFromGitHub = gitHubService.fetchUserRepositories(username);

        // 2. DB에서 저장된(선택된) 레포지토리 이름 조회
        Set<String> selectedRepoNames = repoRepository.findByUsername(username).stream()
                .map(ManagedRepo::getRepository_name)
                .collect(Collectors.toSet());

        // 3. GitHub 데이터 중 DB에 없는 레포지토리 필터링
        return allReposFromGitHub.stream()
                .filter(repo -> !selectedRepoNames.contains(repo.get("name"))) // DB에 없는 레포만 필터링
                .toList();
    }

}
