package com.careerthon.onboardingjava.domain.user.controller;

import com.careerthon.onboardingjava.common.advice.ApiResponse;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignupRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignResponseDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignupResponseDto;
import com.careerthon.onboardingjava.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 회원가입 기능
     * @param requestDto: username, password, nickname
     * @return username, nickname, authorities(UserRole)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(@RequestBody UserSignupRequestDto requestDto) {
        UserSignupResponseDto user = userService.signup(requestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", user));
    }

    /**
     * 로그인 (JWT 토큰 반환)
     * @param requestDto: username, password
     * @return token
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserSignResponseDto>> sign(@RequestBody UserSignRequestDto requestDto) {
        UserSignResponseDto user = userService.sign(requestDto);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", user));
    }
}
