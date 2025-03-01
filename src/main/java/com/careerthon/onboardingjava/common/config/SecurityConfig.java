package com.careerthon.onboardingjava.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    // JwtFilter를 통합하여 JWT 인증을 처리
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())                           // JWT가 상태 비저장이므로 CSRF를 비활성화
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)                 // 상태 비저장 세션 관리
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users", "/api/users/logins", "/auth/tokens").permitAll()     // 화이트리스트 URL
                        .anyRequest().authenticated()                                           // 다른 모든 요청은 인증
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);        // 사용자 지정 JWT 필터 추가

        return http.build();
    }
}
