package org.example.config;

import org.example.oauth.GitHubOAuthUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final GitHubOAuthUserService principalOauthUserService;

    public SecurityConfig(GitHubOAuthUserService principalOauthUserService) {
        this.principalOauthUserService = principalOauthUserService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/","/index.html","/login/oauth2/**").permitAll(); // 로그인 페이지 허용
                    auth.requestMatchers("/home").authenticated(); //home 경로는 인증 필요
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth -> oauth
                        .defaultSuccessUrl("/home",true) // 로그인 성공시 /home으로 Redirect
                        .userInfoEndpoint(userInfo -> userInfo.userService(principalOauthUserService))
                );

        return http.build();
    }
}
