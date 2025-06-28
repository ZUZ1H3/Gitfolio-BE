package com.recode.backend.auth.service;

import com.recode.backend.auth.jwt.TokenProvider;
import com.recode.backend.auth.model.OAuth2UserInfo;
import com.recode.backend.auth.model.LoginResponseDto;
import com.recode.backend.auth.model.OAuth2Properties;
import com.recode.backend.common.exception.CustomException;
import com.recode.backend.common.exception.ErrorCode;
import com.recode.backend.user.UserService;
import com.recode.backend.user.model.User;
import com.recode.backend.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final OAuth2ApiClient oAuth2ApiClient;
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final OAuth2Properties properties;

    public LoginResponseDto authenticate(String code) {
        OAuth2Properties.Provider providerConfig = properties.getProvider().get("github");

        log.info("✅ providerConfig 로드 완료: clientId={}", providerConfig.getClientId());

        // 1. OAuth2 서버에서 access_token 요청
        Map<String, Object> tokenResponse = oAuth2ApiClient.getAccessToken(providerConfig, code);
        String oauthAccessToken = (String) tokenResponse.get("access_token");

        if (oauthAccessToken == null) {
            log.error("❌ access_token을 가져오지 못했습니다.");
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        // 2. access_token으로 사용자 정보 요청
        Map<String, Object> userInfoResponse = oAuth2ApiClient.getUserInfoWithEmail(providerConfig, oauthAccessToken);

        // 3. 프로바이더별 UserInfo 객체 생성 (Factory 패턴)
        OAuth2UserInfo userInfo = new OAuth2UserInfo(userInfoResponse);

        // 4. 사용자 저장 또는 업데이트
        User user = userService.saveOrUpdate(userInfo);
        log.info("✅ 사용자 저장/업데이트 완료: userId={}", user.getId());

        // 5. JWT 토큰 생성 (Spring Security Authentication 사용)
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String jwtAccessToken = tokenProvider.generateAccessToken(authentication);
        String jwtRefreshToken = tokenProvider.generateRefreshToken(authentication, jwtAccessToken);

        // 6. UserDto 생성 및 로그인 응답 반환
        UserDto userDto = UserDto.create(user);

        return new LoginResponseDto(userDto, jwtAccessToken, jwtRefreshToken);
    }
}

