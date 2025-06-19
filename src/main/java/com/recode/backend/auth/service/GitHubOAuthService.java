package com.recode.backend.auth.service;

import com.recode.backend.auth.model.GitHubUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubOAuthService {

    @Value("${github.client-id}")
    private String clientId;

    @Value("${github.client-secret}")
    private String clientSecret;

    @Value("${github.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();

    public GitHubUserDto getUserInfo(String code) {
        // 1. access_token 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<?> entity = new HttpEntity<>(headers);

        String tokenUrl = "https://github.com/login/oauth/access_token"
                + "?client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&code=" + code
                + "&redirect_uri=" + redirectUri;

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, entity, Map.class);

        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // 2. access_token으로 유저 정보 요청
        HttpHeaders authHeader = new HttpHeaders();
        authHeader.setBearerAuth(accessToken);
        authHeader.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<?> userEntity = new HttpEntity<>(authHeader);
        ResponseEntity<Map> userResponse = restTemplate.exchange(
                "https://api.github.com/user", HttpMethod.GET, userEntity, Map.class);

        Map<String, Object> userData = userResponse.getBody();

        return GitHubUserDto.of(userData);
    }
}
