package com.careerthon.onboardingjava.domain.user.dto;

import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class AuthUser {
    private final Long userId;
    private final Collection<? extends GrantedAuthority> userRole;

    public AuthUser(Long userId, UserRole userRole){
        this. userId = userId;
        this.userRole = List.of(new SimpleGrantedAuthority((userRole.name())));
    }
}
