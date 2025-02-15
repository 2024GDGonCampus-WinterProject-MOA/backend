package org.example.service;
// 외부 GitHub API와의 통신을 담당하는 Service
// Redis를 사용하여 사용자별 Github Access Token 관리및 개인 및 조직 레포 통합 반환

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private final RedisTemplate<String, String> redisTemplate;

    public List<Map<String, Object>> fetchUserRepositories(String username) {

        final String accessToken;
        try {
            accessToken = (String) redisTemplate.opsForHash().get("github", username);
            validateTokenScopes(accessToken); // 권한 검증 메소드 추가
        } catch (Exception e) {
            throw new RuntimeException("Access token이 없습니다.");
        }

        List<Map<String, Object>> allRepos = fetchAllpages(
                // 개인 저장소 데이터 (affiliation: owner, collaborator, organization_member)
                "https://api.github.com/user/repos?affiliation=owner,collaborator,organization_member&visibility=all&per_page=100",
                accessToken
        );

        // 사용자가 속한 조직의 저장소 데이터
        fetchAllpages("https://api.github.com/user/orgs?per_page=100", accessToken)
                .stream()
                .map(org -> (String) org.get("login")) // 각 조직 login ID 추출
                .forEach(org -> {
                    String orgUrl = String.format("https://api.github.com/orgs/%s/repos?type=all&per_page=100", org); // 조직 저장소
                    allRepos.addAll(fetchAllpages(orgUrl, accessToken));
                });


        // 중복된 저장소 제거 후, 최종 결과 반환
        return allRepos.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                repo -> (Integer) repo.get("id"), // 고유 ID 기준
                                repo -> {
                                    Map<String, Object> owner = (Map<String, Object>) repo.get("owner");
                                    if ("Organization".equals(owner.get("type"))) { // type: 사용자/조직 구분 ('user'/'Organization')
                                        repo.put("name", repo.get("name") + " (" + owner.get("login") + ")"); // Repo 이름 옆에 (조직명) 추가
                                    }
                                    return repo;
                                },
                                (existing, replacement) -> existing // 중복시, 기존 항목 유지
                        ),
                        map -> new ArrayList<>(map.values())
                ));

    }

    // 토큰(AccessToken) 권한 범위 검증 메소드
    private void validateTokenScopes(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Authorization 헤더에 Bearer 토큰 추가

        // Github API 호출 (사용자 정보 확인)
        ResponseEntity<Map> response = new RestTemplate().exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
        );
        // Github API 응답 헤더에서 X-OAuth-Scopes 확인 (토큰 권한 범위)
        String scopes = response.getHeaders().getFirst("X-OAuth-Scopes"); // X-OAuth-Scopes : 토큰이 필요한 권한을 가지고 있는지 확인
        // X-OAuth-Scopes 형태 -> 'X-OAuth-Scopes: repo, read:org, gist'
        if (scopes == null || !scopes.contains("repo") || !scopes.contains("read:org")) { // repo: 저장소 읽기/쓰기 권한, read:org: 조직 정보 읽기 권한
            throw new SecurityException("Required scopes: repo, read:org"); // 필수 스코프(repo, read:org) 없을시 예외
        }
    }

    // 페이지네이션 메소드
    private List<Map<String, Object>> fetchAllpages(String baseUrl, String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        List<Map<String, Object>> result = new ArrayList<>();
        int page = 1;
        boolean hasMore;

        do {
            String pagedUrl = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "page=" + page;
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    pagedUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                result.addAll(response.getBody()); // 현재 페이지 데이터를 결과 리스트에 추가
                hasMore = !response.getBody().isEmpty(); // 현재 페이지가 비어 있지 않으면 다음 페이지로 이동
                page++;
            } else {
                hasMore = false; // 더 이상 데이터가 없으면 종료
            }
        } while (hasMore);
        return result;
    }
    // TODO: github에서 readme가져와서 string으로 반환
}
