package com.recode.backend.auth.model;

import lombok.Getter;
import lombok.ToString;
import java.util.Map;

@Getter
@ToString
public class OAuth2UserInfo {
    private final String email;
    private final String name;
    private final String login;
    private final String avatarUrl;
    private final Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.email = (String) attributes.get("email");
        this.name = (String) attributes.get("name");
        this.login = (String) attributes.get("login");
        this.avatarUrl = (String) attributes.get("avatar_url");
    }
}

