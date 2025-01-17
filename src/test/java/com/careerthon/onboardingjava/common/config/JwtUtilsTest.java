package com.careerthon.onboardingjava.common.config;

import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JwtUtilsTest {
    private JwtUtils jwtUtil;
    private static final String SECRET_KEY = "KG/TJbUJU+JczbTzvhkcrzjrz+5omW2hMTDVwBw2bvCd8O6Ctkg3eH5FCG7EFMqr\n" +
            "zRhea0tyJMMqSqjd+2bahA==";
    private static final String USER_ID = "1";
    private static final String USERNAME = "testusername";
    private static final String NICKNAME = "testnickname";
    private static final UserRole USER_ROLE = UserRole.ROLE_USER;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtils(Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()));
    }

    @Test
    public void 토큰_생성_테스트() {
        // given
        Long userId = Long.valueOf(USER_ID);
        String username = USERNAME;
        String nickname = NICKNAME;
        UserRole userRole = USER_ROLE;

        // when
        String token = jwtUtil.createToken(userId, username, nickname, userRole);

        //then
        assertNotNull(token);
        System.out.println("토큰: " + token);
    }
}
