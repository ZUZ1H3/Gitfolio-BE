package com.recode.backend.auth.controller;

import com.recode.backend.auth.model.LoginResponseDto;
import com.recode.backend.auth.model.OAuth2LoginRequest;
import com.recode.backend.auth.service.OAuth2Service;
import com.recode.backend.common.success.SuccessDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OAuth2Service oauth2Service;

    @PostMapping("/social")
    public ResponseEntity<?> socialLogin(@RequestBody OAuth2LoginRequest request) {
        LoginResponseDto responseDto = oauth2Service.authenticate(request.getCode());
        return ResponseEntity.ok(new SuccessDataResponse<>(responseDto));
    }
}
