package com.careerthon.onboardingjava.domain.user.dto.respons;

import lombok.Getter;

@Getter
public class GoogleUserInfoDto {
    private final Long id;
    private final String name;

    public GoogleUserInfoDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
