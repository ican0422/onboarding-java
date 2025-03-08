package com.careerthon.onboardingjava.domain.user.controller;

import com.careerthon.onboardingjava.common.advice.ApiResponse;
import com.careerthon.onboardingjava.domain.user.dto.respons.KakaoUserInfoDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.KakaoUserResponse;
import com.careerthon.onboardingjava.domain.user.service.KakaoService;
import com.careerthon.onboardingjava.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoController {
    private final KakaoService kakaoService;
    private final UserService userService;

    /**
     * 카카오 로그인 및 회원가입
     * @param accessCode 카카오톡에서 받은 code
     * @param httpServletResponse 쿠키 저장을 위한 Http
     * @return 유저네임, 닉네임, 유저 역할, JWT 토큰
     */
    @GetMapping("/auth/login/kakao/callback")
    public ResponseEntity<ApiResponse<KakaoUserResponse>> kakaoLogin(@RequestParam("code") String accessCode, HttpServletResponse httpServletResponse) {
        // 토큰을 가져온다.
        String kakaoToken = kakaoService.getAccessToken(accessCode);

        // 토큰을 통해서 유저 정보를 가져온다.
        KakaoUserInfoDto kakaoUser = kakaoService.getKakaoUser(kakaoToken);

        // 유저 정보로 회원가입 or 로그인 한다. (JWT 토큰 반환, 리프레쉬 토큰 지급)
        KakaoUserResponse response = userService.kakaoSignupOrSign(kakaoUser, httpServletResponse);

        // JWT 토큰 반환
        return ResponseEntity.ok(ApiResponse.success("카카오 로그인 성공", response));
    }
}
