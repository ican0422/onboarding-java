package com.careerthon.onboardingjava.domain.user.controller;

import com.careerthon.onboardingjava.common.advice.ApiResponse;
import com.careerthon.onboardingjava.domain.user.dto.request.RefreshTokenRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.RefreshTokenResponseDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignupResponseDto;
import com.careerthon.onboardingjava.domain.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refresh(@RequestBody RefreshTokenRequestDto requestDto) {
        RefreshTokenResponseDto token = tokenService.refresh(requestDto);
        return ResponseEntity.ok(ApiResponse.success("JWT 토큰 재발급", token));
    }
}
