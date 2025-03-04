package com.careerthon.onboardingjava.domain.user.service;

import com.careerthon.onboardingjava.common.config.JwtUtils;
import com.careerthon.onboardingjava.common.exception.JwtValidationResultException;
import com.careerthon.onboardingjava.domain.user.dto.request.RefreshTokenRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.RefreshTokenResponseDto;
import com.careerthon.onboardingjava.domain.user.entity.User;
import com.careerthon.onboardingjava.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtUtils jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;

    /* JWT 토큰 재발급 */
    public RefreshTokenResponseDto refresh(String refreshToken, HttpServletResponse response) {
        // 토큰 검증
        Claims claims = jwtUtil.validateAndExtractClaims(refreshToken);

        // 리프레시 토큰인지 확인
        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new JwtValidationResultException("리프레시 토큰이 아닙니다.");
        }

        // redis에서 토큰 확인
        Long userId = Long.parseLong(claims.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new JwtValidationResultException("사용자를 찾을 수 없습니다."));

        // 새로운 토큰 발급
        String newAccessToken = jwtUtil.createToken(user.getId(), user.getUsername(), user.getNickname(), user.getRole());
        // 새로운 리프레시 토큰 발급
        String newRefreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername(), user.getNickname(), user.getRole());
        // redis 저장
        updateRefreshToken(userId, newRefreshToken, jwtUtil.getRefreshTokenExpirationTime());

        // 새로운 리프레시 토큰을 쿠키에 저장
        int refreshTokenTime = Math.toIntExact(jwtUtil.getRefreshTokenExpirationTime());
        Cookie refreshTokenCookie = seveRefreshTokenCookie(newRefreshToken, refreshTokenTime);
        response.addCookie(refreshTokenCookie);  // 클라이언트에 전송

        return new RefreshTokenResponseDto(newAccessToken);
    }

    /* redis 리프레시 토큰 업데이트 */
    private void updateRefreshToken(Long userId, String newRefreshToken, long expirationTime) {
        // 기존 리프레시 토큰 삭제
        String redisKey = "refreshToken:" + userId;
        redisTemplate.delete(redisKey);

        // 새로운 리프레시 토큰 저장 (만료 시간 설정)
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, expirationTime, TimeUnit.MILLISECONDS);
    }

    /* 새로운 리프레시 토큰을 쿠키에 저장 */
    private Cookie seveRefreshTokenCookie(String refreshToken, int refreshTokenTime) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);               // XSS 공격 방지
        refreshTokenCookie.setSecure(true);                 // HTTPS에서만 전송
        refreshTokenCookie.setPath("/auth/tokens");         // 해당 경로에서만 사용 가능
        refreshTokenCookie.setMaxAge(refreshTokenTime);     // 토큰 일수 int 변환

        return refreshTokenCookie;
    }
}
