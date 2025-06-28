package com.recode.backend.auth.jwt;

import com.recode.backend.common.exception.CustomException;
import com.recode.backend.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveOrUpdate(Long userId, String refreshToken, String accessToken) {
        Token token = tokenRepository.findById(userId)
                .map(existing -> {
                    log.info("🔄 Redis에서 기존 토큰 업데이트: userId={}", userId);
                    existing.updateAccessToken(accessToken);
                    return existing.updateRefreshToken(refreshToken);
                })
                .orElseGet(() -> {
                    log.info("🆕 Redis에 새 토큰 저장: userId={}", userId);
                    return new Token(userId, refreshToken, accessToken);
                });

        tokenRepository.save(token);
        log.info("✅ [TokenService] 저장 완료: userId={}, refreshToken={}, accessToken={}", userId, refreshToken, accessToken);
    }

    public Token findByUserIdOrThrow(Long userId) {
        return tokenRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("❌ [TokenService] Redis에서 토큰 못 찾음: userId={}", userId);
                    return new CustomException(ErrorCode.TOKEN_NOT_FOUND);
                });
    }

    public void updateToken(Long userId, String newAccessToken) {
        Token token = findByUserIdOrThrow(userId);
        token.updateAccessToken(newAccessToken);
        tokenRepository.save(token);
        log.info("🔁 AccessToken 갱신 완료: userId={}, newAccessToken={}", userId, newAccessToken);
    }

    public String findRefreshTokenByUserId(Long userId) {
        Token token = tokenRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NOT_FOUND));
        return token.getRefreshToken();
    }
}
