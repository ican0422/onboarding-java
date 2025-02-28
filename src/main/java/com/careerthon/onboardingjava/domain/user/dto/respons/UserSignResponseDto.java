package com.careerthon.onboardingjava.domain.user.dto.respons;

import lombok.Getter;

@Getter
public class UserSignResponseDto {
    private final String token;
    private final String refreshToken;

    public UserSignResponseDto(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
