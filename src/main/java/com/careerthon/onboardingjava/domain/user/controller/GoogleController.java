package com.careerthon.onboardingjava.domain.user.controller;

import com.careerthon.onboardingjava.common.advice.ApiResponse;
import com.careerthon.onboardingjava.domain.user.dto.respons.GoogleUserInfoDto;
import com.careerthon.onboardingjava.domain.user.service.GoogleService;
import com.careerthon.onboardingjava.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoogleController {
    private final GoogleService googleService;
    private final UserService userService;

    @GetMapping("auth/login/google/callback")
    public ResponseEntity<ApiResponse<?>> googleLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        // 토큰 가져오기
        String googleToken = googleService.getAccessToken(accessCode);

        // 토큰을 통해서 사용자 정보 불러오기
        GoogleUserInfoDto googleUser = googleService.getGoogleUser(googleToken);

        // 유저 정보로 회원가입 or 로그인 한다. (JWT 토큰 반환, 리프레쉬 토큰 지급)


        // JWT 토큰 반환
        return null;
    }
}
