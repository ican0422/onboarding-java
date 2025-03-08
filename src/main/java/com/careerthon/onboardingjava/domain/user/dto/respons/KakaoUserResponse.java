package com.careerthon.onboardingjava.domain.user.dto.respons;

import com.careerthon.onboardingjava.domain.user.dto.Authorities;
import com.careerthon.onboardingjava.domain.user.entity.User;
import lombok.Getter;

@Getter
public class KakaoUserResponse {
    private final String username;
    private final String nickname;
    private final Authorities authorities;
    private final String token;

    public KakaoUserResponse(User user, Authorities authorities, String token) {
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.authorities = authorities;
        this.token = token;
    }
}
