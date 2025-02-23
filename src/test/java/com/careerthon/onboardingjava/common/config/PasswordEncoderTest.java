package com.careerthon.onboardingjava.common.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordEncoderTest {
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new PasswordEncoder();
    }

    @Test
    public void 비밀번호_암호화() {
        // given
        String rawPassword = "123456";

        // when
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // then
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(encodedPassword.startsWith("$2a$"), "BCrypt 해싱 문자열이 맞는지 확인");
        System.out.println(encodedPassword);
    }

    @Test
    public void 비밀번호_일치() {
        // given
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when
        boolean verified = passwordEncoder.verify(rawPassword, encodedPassword);

        // then
        assertTrue(verified, "비밀번호가 일치해야 함");
    }

    @Test
    public void 비밀번호_불일치() {
        // given
        String rawPassword = "123456";
        String wrongPassword = "1234567";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when
        boolean verified = passwordEncoder.verify(wrongPassword, encodedPassword);

        // then
        assertFalse(verified, "비밀번호 불일치해야 함");
    }
}
