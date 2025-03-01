package com.careerthon.onboardingjava.domain.user.controller;

import com.careerthon.onboardingjava.common.advice.ApiResponse;
import com.careerthon.onboardingjava.domain.user.dto.request.RefreshTokenRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.RefreshTokenResponseDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignupResponseDto;
import com.careerthon.onboardingjava.domain.user.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/tokens")
@RequiredArgsConstructor
public class TokenController {
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        RefreshTokenResponseDto token = tokenService.refresh(refreshToken, response);
        return ResponseEntity.ok(ApiResponse.success("JWT 토큰 재발급", token));
    }
}
