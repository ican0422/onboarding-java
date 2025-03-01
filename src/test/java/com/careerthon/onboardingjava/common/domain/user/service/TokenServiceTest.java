package com.careerthon.onboardingjava.common.domain.user.service;

import com.careerthon.onboardingjava.common.config.JwtUtils;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.RefreshTokenResponseDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignResponseDto;
import com.careerthon.onboardingjava.domain.user.entity.User;
import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import com.careerthon.onboardingjava.domain.user.repository.UserRepository;
import com.careerthon.onboardingjava.domain.user.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private HttpServletResponse responses; // HttpServletResponse 모킹 (쿠키 추가)

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setup() {
        // 256비트 이상의 Secret Key 생성
        String secretKey = Base64.getEncoder().encodeToString("thisIsASecretKeyForJWTWithAtLeast256Bits!".getBytes());

        JwtUtils jwtUtil = new JwtUtils(secretKey);
        ReflectionTestUtils.setField(tokenService, "jwtUtil", jwtUtil);
    }

    @Test
    public void 엑세스_토큰_재발급() {
        // given
        long userId = 1L;
        String username = "testUsername";
        String password = "testPassword";
        String nickname = "testNickname";
        UserRole userRole = UserRole.ROLE_USER;

        String oldRefreshToken = Jwts.builder()
                .setSubject(String.valueOf(userId))  // 유저 ID를 subject로 설정
                .claim("tokenType", "refresh")
                .signWith(SignatureAlgorithm.HS256, "thisIsASecretKeyForJWTWithAtLeast256Bits!".getBytes())  // 서명 추가
                .compact();

        // 유저 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "username", username);
        ReflectionTestUtils.setField(user, "password", password);
        ReflectionTestUtils.setField(user, "nickname", nickname);
        ReflectionTestUtils.setField(user, "role", userRole);

        // 유저 가져오기
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        // redis 저장
        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        // 쿠키
        doNothing().when(responses).addCookie(any(Cookie.class));

        // when
        RefreshTokenResponseDto response = tokenService.refresh(oldRefreshToken, responses);

        // then
        assertNotNull(response);
        System.out.println(response.getToken());

        // 쿠키가 정상적으로 추가되었는지 검증
        verify(responses, times(1)).addCookie(any(Cookie.class));
    }
}
