package com.careerthon.onboardingjava.domain.user.dto.request;

import lombok.Getter;

@Getter
public class UserSignupRequestDto {
    private String username;
    private String password;
    private String nickname;
}
