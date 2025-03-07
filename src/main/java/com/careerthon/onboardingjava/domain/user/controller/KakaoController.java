package com.careerthon.onboardingjava.domain.user.controller;

import com.careerthon.onboardingjava.common.advice.ApiResponse;
import com.careerthon.onboardingjava.domain.user.service.KakaoService;
import com.careerthon.onboardingjava.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;
    private final UserService userService;

    @GetMapping("/auth/login/kakao/callback")
    public ResponseEntity<ApiResponse<?>> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        User user = userService.kakaoLogin();
    }
}
