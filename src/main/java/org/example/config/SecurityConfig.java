package org.example.config;

import java.util.Arrays;
import org.example.oauth.GitHubOAuthUserService;
import org.example.utils.CustomSuccessHandler;
import org.example.utils.JwtFilter;
import org.example.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final GitHubOAuthUserService principalOauthUserService;
    private final CustomSuccessHandler customSuccessHandler;

    public SecurityConfig(GitHubOAuthUserService principalOauthUserService, CustomSuccessHandler customSuccessHandler) {
        this.principalOauthUserService = principalOauthUserService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(
                Arrays.asList("http://localhost:3000", "http://localhost:5173", "https://moa.klr.kr"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS"));
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin",
                        "Access-Control-Request-Method", "Access-Control-Request-Headers", "Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterBefore(new JwtFilter(jwtUtil, principalOauthUserService), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/","/index.html","/login/oauth2/**", "favicon.ico").permitAll(); // 로그인 페이지 허용
                    auth.requestMatchers("/oauth2/**", "/login/**").permitAll();
                    auth.requestMatchers("/swagger/**", "/swagger-ui/**","/v3/api-docs/**").permitAll();
                    auth.requestMatchers("/home").authenticated(); //home 경로는 인증 필요
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(principalOauthUserService))
                        .successHandler(customSuccessHandler));

        return http.build();
    }
}
