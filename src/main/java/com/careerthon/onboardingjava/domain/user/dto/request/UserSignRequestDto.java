package com.careerthon.onboardingjava.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSignRequestDto {
    private String username;
    private String password;
}
