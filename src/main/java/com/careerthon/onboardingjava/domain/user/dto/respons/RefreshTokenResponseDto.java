package com.careerthon.onboardingjava.domain.user.dto.respons;

import lombok.Getter;

@Getter
public class RefreshTokenResponseDto {
    private final String token;

    public RefreshTokenResponseDto(String token) {
        this.token = token;
    }
}
