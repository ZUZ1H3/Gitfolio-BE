package com.recode.backend.github.model;

import lombok.Data;

@Data
public class GitHubProfileDto {
    private String name;
    private String login;
    private int followers;
    private int following;
    private int publicRepos;
}