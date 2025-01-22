package org.example.service;
//GitHub 데이터를 가공, DB와 상호작용하는 비즈니스 로직 처리

import org.example.dto.RepositoryResponseDto;
import org.example.dto.RepositorySaveRequestDto;
import org.example.dto.RepositoryUpdateRequestDto;
import org.example.entity.ManagedRepository;
import org.example.repository.RepositoryManager;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RepositoryService {
    private final RepositoryManager repositoryManager;
    private final GitHubService gitHubService;

    public RepositoryService(RepositoryManager repositoryManager, GitHubService gitHubService) {
        this.repositoryManager = repositoryManager;
        this.gitHubService = gitHubService;
    }


    // 선택한 Repository 저장 메소드
    public void saveSelectedRepository(String username, RepositorySaveRequestDto requestDto) {
        ManagedRepository managedRepository = new ManagedRepository();
        //OAuth2에서 가져온 username 설정
        managedRepository.setUsername(username);

        // DTO 데이터를 Entity에 매핑
        managedRepository.setRepository_name(requestDto.getName());
        managedRepository.setRepository_url(requestDto.getHtmlUrl());
        managedRepository.setDescription(requestDto.getDescription());

        // 날짜 정보
        managedRepository.setCreatedAt(LocalDateTime.parse(requestDto.getCreatedAt()));
        managedRepository.setUpdatedAt(LocalDateTime.parse(requestDto.getUpdatedAt()) );
        managedRepository.setPushedAt(LocalDateTime.parse(requestDto.getPushedAt()));

        //DB 저장
        repositoryManager.save(managedRepository);
    }


    // 모든 Repository 조회 로직
    public List<RepositoryResponseDto> getAllRepositories(String username) {
        List<ManagedRepository> repositories = repositoryManager.findByUsername(username);

        // entity 리스트를 DTO 리스트로 변환하여 반환
        return repositories.stream()
                .map(repo -> new RepositoryResponseDto(
                        repo.getId(),
                        repo.getRepository_name(),
                        repo.getRepository_url(),
                        repo.getDescription()
                ))
                .toList();
    }


    // Repository 이름, 설명, Url 변경 메소드
    public void updateRepository(Long id, RepositoryUpdateRequestDto requestDto) {
        // 기존 데이터 가져오기
        ManagedRepository repository = this.repositoryManager.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid Repository ID")); //데이터 없는 경우 예외처리

        // Repository 이름 업데이트
        if(requestDto.getRepositoryName() != null && !requestDto.getRepositoryName().isEmpty()){
            repository.setRepository_name(requestDto.getRepositoryName());
        }else{
            System.out.println("Repository name is null or empty. Keeping the existing value.");
        }

        // 설명 업데이트
        if(requestDto.getDescription() != null && !requestDto.getDescription().isEmpty()){
            repository.setDescription(requestDto.getDescription());
        }else{
            System.out.println("Description is null or empty. Keeping the existing value.");
        }

        // Repository URL 업데이트
        if(requestDto.getRepositoryUrl() != null && !requestDto.getRepositoryUrl().isEmpty()){
            repository.setRepository_url(requestDto.getRepositoryUrl());
        }else{
            System.out.println("Repository URL is null or empty. Keeping the existing value.");
        }

        // 변경된 엔티티 저장
        this.repositoryManager.save(repository);
    }

    // Repository 삭제 로직
    public void deleteRepository(Long id) {
        repositoryManager.deleteById(id);
    }

}
