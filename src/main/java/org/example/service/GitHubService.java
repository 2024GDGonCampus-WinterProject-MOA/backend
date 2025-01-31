package org.example.service;
// 외부 GitHub API와의 통신을 담당하는 Service
// 사용자 리포지토리 데이터 가져옴

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubService {
    private final RedisTemplate<String, String> redisTemplate;

    public List<Map<String, Object>> fetchUserRepositories(String username) {
        String accessToken = null;
        try {
            accessToken = (String) redisTemplate.opsForHash().get("github", username);
        } catch (Exception e) {
            throw new RuntimeException("Access token이 없습니다.");
        }
        String apiUrl = "https://api.github.com/user/repos?affiliation=owner,collaborator,organization_member&per_page=100&page=1";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        return response.getBody();
    }

    // TODO: github에서 readme가져와서 string으로 반환
}
