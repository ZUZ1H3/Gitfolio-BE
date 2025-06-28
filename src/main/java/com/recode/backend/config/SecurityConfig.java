package com.recode.backend.config;

import com.recode.backend.auth.jwt.filter.TokenAuthenticationFilter;
import com.recode.backend.auth.jwt.filter.TokenExceptionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) //기본 인증 로그인 비활성화
                .csrf(AbstractHttpConfigurer::disable) //csrf 비활성화
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",                 // 루트 경로
                                "/index.html",       // 정적 HTML
                                "/css/**", "/js/**", "/images/**", // 정적 리소스
                                "/api/**"            // API 경로
                        ).permitAll()
                        .anyRequest().authenticated()
                )            // TokenExceptionFilter를 JwtAuthenticationFilter 전에 등록
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), TokenAuthenticationFilter.class);
        return http.build();
    }
}
