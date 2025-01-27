package org.example.utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.oauth.GitHubUserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        GitHubUserDetails githubUserDetail = (GitHubUserDetails) authentication.getPrincipal();
        String username = githubUserDetail.getName();

        String token = jwtUtil.createToken(username); // 10시간

        response.addHeader(HttpHeaders.SET_COOKIE, jwtUtil.createCookie("Authorization", token).toString());

//        response.sendRedirect("http://localhost:8080/home");
        response.sendRedirect("http://localhost:5173/");
    }
}
