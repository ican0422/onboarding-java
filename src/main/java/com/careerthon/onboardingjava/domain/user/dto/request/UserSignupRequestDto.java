package com.careerthon.onboardingjava.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignupRequestDto {
    private String username;
    private String password;
    private String nickname;
}
