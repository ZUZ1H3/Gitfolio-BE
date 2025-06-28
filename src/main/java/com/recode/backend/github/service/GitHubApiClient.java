package com.recode.backend.github.service;

import com.recode.backend.github.model.GitHubProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    public GitHubProfileDto getUserProfile(String accessToken) {
        String url = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        Map<String, Object> body = response.getBody();

        GitHubProfileDto dto = new GitHubProfileDto();
        dto.setName((String) body.get("name"));
        dto.setLogin((String) body.get("login"));
        dto.setFollowers((Integer) body.get("followers"));
        dto.setFollowing((Integer) body.get("following"));
        dto.setPublicRepos((Integer) body.get("public_repos"));

        return dto;
    }
}
