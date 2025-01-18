package com.careerthon.onboardingjava.common.config;

import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    public void 토큰_검증_테스트() {
        // given
        Long userId = Long.valueOf(USER_ID);
        String username = USERNAME;
        String nickname = NICKNAME;
        UserRole userRole = USER_ROLE;
        String token = jwtUtil.createToken(userId, username, nickname, userRole);

        // when
        Optional<Claims> claimsOptional = jwtUtil.validateAndExtractClaims(token);
        Claims claims = claimsOptional.get();

        UserRole actualUserRole = UserRole.valueOf(claims.get("userRole").toString());

        // 만료 시간 계산
        Date expiration = claims.getExpiration();
        long remainingMillis = expiration.getTime() - System.currentTimeMillis();
        long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis); // 남은 시간(분 단위)

        // then
        assertNotNull(claimsOptional.isPresent());
        // Claim 값 검증
        assertEquals(USER_ID, claims.getSubject());
        assertEquals(username, claims.get("userName"));
        assertEquals(nickname, claims.get("nickName"));
        assertEquals(userRole, actualUserRole);

        System.out.println("검증된 클레임" + claims);
        System.out.println("토큰 만료까지 남은 일 수: " + remainingMinutes);
    }

    @Test
    public void 리프레시_토큰_생성_테스트() {
        // given
        Long userId = Long.valueOf(USER_ID);
        String username = USERNAME;
        String nickname = NICKNAME;
        UserRole userRole = USER_ROLE;

        // when
        String token = jwtUtil.createRefreshToken(userId, username, nickname, userRole);

        //then
        assertNotNull(token);
        System.out.println("토큰: " + token);
    }

    @Test
    public void 리프레시_토큰_검증_테스트() {
        // given
        Long userId = Long.valueOf(USER_ID);
        String username = USERNAME;
        String nickname = NICKNAME;
        UserRole userRole = USER_ROLE;
        String token = jwtUtil.createRefreshToken(userId, username, nickname, userRole);

        // when
        Optional<Claims> claimsOptional = jwtUtil.validateAndExtractClaims(token);
        Claims claims = claimsOptional.get();

        UserRole actualUserRole = UserRole.valueOf(claims.get("userRole").toString());

        // 만료 시간 계산
        Date expiration = claims.getExpiration();
        long remainingMillis = expiration.getTime() - System.currentTimeMillis();
        long remainingDays = TimeUnit.MILLISECONDS.toDays(remainingMillis); // 남은 시간(일 단위)

        // then
        assertNotNull(claimsOptional.isPresent());
        // Claim 값 검증
        assertEquals(USER_ID, claims.getSubject());
        assertEquals(username, claims.get("userName"));
        assertEquals(nickname, claims.get("nickName"));
        assertEquals(userRole, actualUserRole);

        System.out.println("검증된 클레임" + claims);
        System.out.println("토큰 만료까지 남은 일 수: " + remainingDays);
    }
}
