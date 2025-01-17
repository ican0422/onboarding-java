package com.careerthon.onboardingjava.domain.user.dto.respons;

import com.careerthon.onboardingjava.domain.user.dto.Authorities;
import com.careerthon.onboardingjava.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserSignupResponseDto {
    private final String username;
    private final String nickname;
    private final Authorities authorities;

    public UserSignupResponseDto(User user, Authorities authorities) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = authorities;
    }
}
