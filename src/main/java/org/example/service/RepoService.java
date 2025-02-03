package org.example.service;
//GitHub 데이터를 가공, DB와 상호작용하는 비즈니스 로직 처리

import lombok.RequiredArgsConstructor;
import org.example.dto.RepositoryResponseDto;
import org.example.dto.RepositorySaveRequestDto;
import org.example.dto.RepositoryUpdateRequestDto;
import org.example.dto.SingleRepositoryResponseDto;
import org.example.entity.ManagedRepo;
import org.example.repository.RepoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RepoService {
    private final RepoRepository repoRepository;
    private final RestTemplate restTemplate;
    private static final DateTimeFormatter DATE_FORMATTER  = DateTimeFormatter.ofPattern("yyyy.MM");

    //개발 기간 표현(yyyy.MM ~ yyyy.MM or 현재) 메소드
    private String DevPeriod(ManagedRepo repo) {
        String startDate = repo.getCreatedAt().format(DATE_FORMATTER);
        String endDate = repo.getDevStatus().equals("개발완료")
                ? repo.getPushedAt().format(DATE_FORMATTER)
                : "현재";
        return String.format("%s ~ %s", startDate, endDate);
    }

    @Transactional
    public List<ManagedRepo> saveSelectedRepositories(String username, List<RepositorySaveRequestDto> requestDtos) {
        // DTO 리스트를 Entity 리스트로 변환하여 저장
        return requestDtos.stream()
                .map(requestDto -> saveSelectedRepository(username, requestDto))
                .toList();
    }

    // 선택한 Repository 저장 메소드
    @Transactional
    public ManagedRepo saveSelectedRepository(String username, RepositorySaveRequestDto requestDto) {
        ManagedRepo managedRepo = new ManagedRepo();
        //OAuth2에서 가져온 username 설정
        managedRepo.setUsername(username);

        // DTO 데이터를 Entity에 매핑
        managedRepo.setRepository_name(requestDto.getName());
        managedRepo.setRepository_url(requestDto.getHtmlUrl());
        managedRepo.setDescription(requestDto.getDescription());

        // 날짜 정보
        managedRepo.setCreatedAt(LocalDateTime.parse(requestDto.getCreatedAt()));
        managedRepo.setUpdatedAt(LocalDateTime.parse(requestDto.getUpdatedAt()) );
        managedRepo.setPushedAt(LocalDateTime.parse(requestDto.getPushedAt()));

        managedRepo.setDevStatus("개발중"); // 초기 상태 설정

        //DB 저장
        return repoRepository.save(managedRepo);
    }

    // 모든 Repository 조회 로직
    public List<RepositoryResponseDto> getAllRepositories(String username) {
        List<ManagedRepo> repositories = repoRepository.findByUsername(username);

        // entity 리스트를 DTO 리스트로 변환하여 반환
        return repositories.stream()
                .map(repo -> new RepositoryResponseDto(
                        repo.getId(),
                        repo.getRepository_name(),
                        repo.getDevStatus(),
                        DevPeriod(repo), // 개발 기간 추가
                        repo.getProjectType() // 프로젝트 종류 포함
                ))
                .toList();
    }

    // 단일 Repository 조회 로직
    public SingleRepositoryResponseDto getRepository(Long id) {
        ManagedRepo repository = this.repoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invalid Repository ID"));

        return new SingleRepositoryResponseDto(
                repository.getId(),
                repository.getRepository_name(),
                repository.getRepository_url(),
                repository.getDescription(),
                repository.getDevStatus(),
                DevPeriod(repository), // 개발 기간 추가
                repository.getProjectType() // 프로젝트 종류 포함
        );
    }

    // Repository 이름, 설명, Url, Project Type, 개발 기간, 개발 상태 변경 메소드
    @Transactional
    public void updateRepository(Long id, RepositoryUpdateRequestDto requestDto) {
        // 기존 데이터 가져오기
        ManagedRepo repository = this.repoRepository.findById(id)
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

        // Project Type 업데이트
        if (requestDto.getProjectType() != null) {
            repository.setProjectType(requestDto.getProjectType());
        }

        // "개발 완료"로 업데이트시, endtime이 "현재"에서 최근 푸쉬시간으로 업데이트
        if(requestDto.getDevStatus() != null){
            String newStatus = requestDto.getDevStatus();
            repository.setDevStatus(newStatus);

            if("개발완료".equals(newStatus)){
                LocalDateTime latestPushedAt = fetchLatestPushedAtFromGitHub(repository.getRepository_url());
                repository.setPushedAt(latestPushedAt);
            }
        }
        // 변경된 엔티티 저장
        this.repoRepository.save(repository);
    }

    // Github API 호출하여 최근 푸쉬시간 가져옴
    private LocalDateTime fetchLatestPushedAtFromGitHub(String repourl) {
        String apiUrl = repourl.replace("https://github.com/", "https://api.github.com/repos/");
        // GitHub API 호출
        ResponseEntity<Map> response = restTemplate.getForEntity(repourl, Map.class);

        // pushed_at 값 추출 및 파싱
        String pushedAtStr = (String) response.getBody().get("pushed_at");
        return LocalDateTime.parse(pushedAtStr);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Repository 삭제 로직
    public void deleteRepository(Long id) {
        repoRepository.deleteById(id);
    }

}
