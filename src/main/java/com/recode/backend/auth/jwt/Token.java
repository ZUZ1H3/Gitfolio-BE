package com.recode.backend.auth.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(value = "jwt", timeToLive = 60 * 60 * 24 * 7)
public class Token {

    @Id
    private Long id;
    private String refreshToken;
    private String accessToken;

    public Token(Long id, String refreshToken, String accessToken) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Token updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    // 👉 Setter 직접 넣거나, Lombok의 @Setter 써도 됨 (필드에 붙이지 말고 메서드로!)

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}