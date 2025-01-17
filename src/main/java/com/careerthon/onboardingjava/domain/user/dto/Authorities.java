package com.careerthon.onboardingjava.domain.user.dto;

import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import lombok.Getter;

@Getter
public class Authorities {
    private final UserRole authorityName;

    public Authorities(UserRole userRole) {
        this.authorityName = userRole;
    }
}
