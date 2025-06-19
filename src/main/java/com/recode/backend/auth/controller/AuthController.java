package com.recode.backend.auth.controller;

import com.recode.backend.auth.model.GitHubUserDto;
import com.recode.backend.auth.service.GitHubOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    private final GitHubOAuthService gitHubOAuthService;

    // 로그인 → GitHub 로그인 창으로 이동
    @GetMapping("/login")
    public ResponseEntity<Void> login() {
        String githubUrl = "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&scope=read:user user:email";
        return ResponseEntity.status(302).header("Location", githubUrl).build();
    }

    // 로그인 성공 후 code를 받아서 사용자 정보 요청
    @GetMapping("/callback")
    public ResponseEntity<GitHubUserDto> callback(@RequestParam String code) {
        GitHubUserDto user = gitHubOAuthService.getUserInfo(code);
        return ResponseEntity.ok(user); // 여기서 JWT 발급도 가능
    }
}

