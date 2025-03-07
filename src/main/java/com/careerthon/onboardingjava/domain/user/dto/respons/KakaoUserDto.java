package com.careerthon.onboardingjava.domain.user.dto.respons;

import lombok.Getter;

@Getter
public class KakaoUserDto {
    private final Long id;
    private final String nickname;

    public KakaoUserDto(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
