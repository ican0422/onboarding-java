package com.careerthon.onboardingjava.domain.user.dto.respons;

import lombok.Getter;

@Getter
public class UserSignResponseDto {
    private final String token;

    public UserSignResponseDto(String token) {
        this.token = token;
    }
}
