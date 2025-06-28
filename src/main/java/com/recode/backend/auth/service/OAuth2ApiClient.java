package com.recode.backend.auth.service;

import com.recode.backend.auth.model.OAuth2Properties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class OAuth2ApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    public Map<String, Object> getAccessToken(OAuth2Properties.Provider providerConfig, String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", providerConfig.getClientId());
        params.add("client_secret", providerConfig.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", providerConfig.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);
        return response.getBody();
    }


    // 사용자 정보 요청
    public Map<String, Object> getUserInfo(OAuth2Properties.Provider providerConfig, String accessToken) {
        String userInfoUrl = "https://api.github.com/user";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

        return response.getBody();
    }

    public Map<String, Object> getUserInfoWithEmail(OAuth2Properties.Provider providerConfig, String accessToken) {
        Map<String, Object> userInfo = getUserInfo(providerConfig, accessToken);

        String email = getPrimaryEmail(accessToken);
        userInfo.put("email", email);

        return userInfo;
    }

    public String getPrimaryEmail(String accessToken) {
        String emailsUrl = "https://api.github.com/user/emails";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(emailsUrl, HttpMethod.GET, entity, List.class);
        List<Map<String, Object>> emails = response.getBody();

        if (emails == null || emails.isEmpty()) {
            return null;
        }

        return emails.stream()
                .filter(email -> Boolean.TRUE.equals(email.get("primary")) && Boolean.TRUE.equals(email.get("verified")))
                .map(email -> (String) email.get("email"))
                .findFirst()
                .orElse(null);
    }

}
