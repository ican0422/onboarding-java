package com.careerthon.onboardingjava.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    ROLE_USER(Authority.USER),
    ROLE_ADMIN(Authority.ADMIN);

    private final String userRole;

    public static class Authority{
        public static final String USER = "USER";
        public static final String ADMIN = "ADMIN";
    }
}
