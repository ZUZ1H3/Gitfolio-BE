package com.recode.backend.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GitHubUserDto {
    private String login;
    private String name;
    private String email;
    private String avatarUrl;

    public static GitHubUserDto of(Map<String, Object> data) {
        return new GitHubUserDto(
                (String) data.get("login"),
                (String) data.get("name"),
                null, // GitHub는 이메일을 기본으로 안 줌
                (String) data.get("avatar_url")
        );
    }
}
