package com.careerthon.onboardingjava.common.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/* Authentication 커스텀 객체 생성 */
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public JwtAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities); // setAuthenticated(true) 직접 호출 X
    }
}
