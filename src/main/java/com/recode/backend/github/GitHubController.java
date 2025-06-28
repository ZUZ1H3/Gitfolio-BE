package com.recode.backend.github;

import com.recode.backend.github.model.GitHubContributionsDto;
import com.recode.backend.github.model.GitHubProfileDto;
import com.recode.backend.github.service.GitHubApiClient;
import com.recode.backend.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
public class GitHubController {

    private final GitHubApiClient gitHubApiClient;

    @GetMapping("/profile")
    public GitHubProfileDto getProfile(
            @AuthenticationPrincipal User user,
            @RequestHeader("X-GitHub-Token") String githubToken
    ) {
        return gitHubApiClient.getUserProfile(githubToken);
    }

    @GetMapping("/contributions")
    public GitHubContributionsDto getContributions(
            @AuthenticationPrincipal User user,
            @RequestHeader("X-GitHub-Token") String githubToken
    ) {
        return gitHubApiClient.getUserContributions(githubToken, user.getLogin());
    }
}
