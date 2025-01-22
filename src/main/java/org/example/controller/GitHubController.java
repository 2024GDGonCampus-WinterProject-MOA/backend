package org.example.controller;


import org.example.oauth.GitHubUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GitHubController {

    @GetMapping("/login")
    public String loginPage() {
        return "index"; // index.html 파일 렌더링
    }

    @GetMapping("/home")
    public String home(Model model, Authentication auth) {
        GitHubUserDetails userDetails = (GitHubUserDetails) auth.getPrincipal();
        model.addAttribute("user", userDetails);
        return "home"; // home.html 파일 렌더링
    }

}
