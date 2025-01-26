package org.example.controller;


import org.example.oauth.GitHubUserDetails;
import org.example.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
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
    public String home(Model model) {
        String username = JwtUtil.getUsernameFromContext();
        if (username == null) {
            System.out.println("username이 없습니다.");
            return "home";
        }
        // 사용자 정보를 모델에 추가
        model.addAttribute("username", username); // GitHub username
        model.addAttribute("nickname", "-");  // GitHub nickname
        model.addAttribute("email", "-");                      // 이메일
        model.addAttribute("profileImg", "-");            // 프로필 이미지 URL
        model.addAttribute("role", "-"); // 권한

        return "home"; // home.html 파일 렌더링
    }

}
