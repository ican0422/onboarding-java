package com.careerthon.onboardingjava.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Optional;

@Component
@Slf4j
public class JwtUtils {
    private  final SecretKey key;
    private static final String BEARER = "Bearer ";

    // JWT 키 초기화
    public JwtUtils(@Value("${jwt.secret.key}") String secret) {
        // Base64 디코딩 추가
        byte[] keyBytes = Base64.getDecoder().decode(secret);

        // 키 생성
        this.key = Keys.hmacShaKeyFor(keyBytes);

        // 키 초기화 확인용 로그
        log.info("JWT Secret Key: {}", this.key);
    }

    // JWT 토큰만 추출
    public Optional<String> substringToken(String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith(BEARER)) {
            return Optional.empty();
        }
        return Optional.of(tokenHeader.substring(BEARER.length()));
    }

    // 클래임 추출
    public Optional<Claims> extractClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 성공 로그
            log.info("JWT Claims extracted successfully: {}", claims);
            return Optional.of(claims);
        // 실패 로그
        } catch (ExpiredJwtException e) {
            log.warn("JWT 토큰 만료: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT 토큰 서명이 일치하지 않습니다: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT 토큰 구문 분석 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT 클레임을 추출하는 동안 예상치 못한 오류 발생: {}", e.getMessage());
        }
        return Optional.empty();
    }
}
