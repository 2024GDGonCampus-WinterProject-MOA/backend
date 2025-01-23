package org.example.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GitHubOAuthUserService extends DefaultOAuth2UserService { //OAuth2 사용자 정보 로드 서비스 상속

    private final GitHubUserRepository gitHubUserRepository;

    public GitHubOAuthUserService(final GitHubUserRepository gitHubUserRepository) {
        this.gitHubUserRepository = gitHubUserRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { // GitHub API로부터 사용자 정보 가져옴
        // 사용자 정보를 가져옴.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // GitHub에서 받은 사용자 정보 추출
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String username = (String) attributes.get("login"); // GitHub username (고유 식별자)
        String nickname = (String) attributes.get("name");  // GitHub nickname (표시 이름)
        String email = (String) attributes.get("email");   // 이메일 주소 (nullable)
        String profileImg = (String) attributes.get("avatar_url"); // 프로필 이미지 URL

        // DB 사용자 저장 또는 업데이트
        GitHubUser user = gitHubUserRepository.findByUsername(username)
                .orElseGet(GitHubUser::new);  //기존 사용자가 없으면 새로 생성하기

        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setProfileImg(profileImg);
        user.setRole(GitHubUser.Role.ROLE_USER); // 기본 권한 설정

        // DB 저장
        gitHubUserRepository.save(user);

        // GitHubUserDetails 객체 생성 및 반환
        return new GitHubUserDetails(user, attributes);
    }

}

