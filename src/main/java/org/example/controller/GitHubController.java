package org.example.controller;


import org.example.oauth.GitHubUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GitHubController {

    // 로그인 페이지
    @GetMapping("/")
    public String loginPage() {
        return "index"; // index.html 파일 렌더링
    }

    // 홈 페이지 (로그인 후)
    @GetMapping("/home")
    public String home(Model model, Authentication auth) {
        // Authentication 객체에서 사용자 정보 가져오기
        Object principal = auth.getPrincipal();

        // GitHubUserDetails로 캐스팅하여 사용자 정보 확인
        if (principal instanceof GitHubUserDetails) {
            GitHubUserDetails userDetails = (GitHubUserDetails) principal;

            // 사용자 정보를 모델에 추가
            model.addAttribute("username", userDetails.getAttributes().get("login")); // GitHub username
            model.addAttribute("nickname", userDetails.getAttributes().get("name"));  // GitHub nickname
            model.addAttribute("email", userDetails.getEmail());                      // 이메일
            model.addAttribute("profileImg", userDetails.getAvatarUrl());            // 프로필 이미지 URL
            model.addAttribute("role", userDetails.getAuthorities().iterator().next().getAuthority()); // 권한
        }

        return "home"; // home.html 파일 렌더링
    }

}
