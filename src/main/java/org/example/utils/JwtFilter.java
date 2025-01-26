package org.example.utils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.oauth.GitHubOAuthUserService;
import org.example.oauth.GitHubUser;
import org.example.oauth.GitHubUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final GitHubOAuthUserService gitHubOAuthUserService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractTokenFromCookie(request);
            if (token == null || jwtUtil.isTokenExpired(token)) {
                filterChain.doFilter(request, response);
                return;
            }
            Authentication auth = createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception e) {
            System.out.println("로그인 중 오류가 발생했습니다: "+e.getMessage());
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private Authentication createAuthentication(String token){
        String username = jwtUtil.getUsername(token);

        GitHubUser gitHubUser= gitHubOAuthUserService.findByUsername(username);
        GitHubUserDetails githubUserDetail = new GitHubUserDetails(gitHubUser, null);
        String role = gitHubUser.getRole().name();
        return new UsernamePasswordAuthenticationToken(githubUserDetail, null, List.of(() -> role));
    }

}