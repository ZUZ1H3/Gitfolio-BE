package com.recode.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) //기본 인증 로그인 비활성화
                .csrf(AbstractHttpConfigurer::disable) //csrf 비활성화
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용하지 않음
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll() // API는 모두 허용
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
