package com.careerthon.onboardingjava.common.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter implements Filter {
    private final JwtUtils jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();

        // 토큰 검사 패스
        if (url.equals("/api/users") || url.equals("/api/users/logins")) {
            chain.doFilter(request, response); // JWT 토큰 검사 제외
            return;
        }

        // Authorization 헤더에서 토큰 추출
        String tokenHeader = httpRequest.getHeader("Authorization");
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            handleForbiddenResponse(httpResponse);
            return;
        }

        // 토큰 검증
        try {
            String token = jwtUtil.substringToken(tokenHeader)
                    .orElseThrow(() -> new IllegalArgumentException("JWT 토큰이 비어 있습니다."));
            Claims claims = jwtUtil.validateAndExtractClaims(token)
                    .orElseThrow(() -> new IllegalArgumentException("Claims 추출 실패했습니다."));
            boolean isTokenValid = jwtUtil.isTokenValid(token);

            if (!isTokenValid) {
                log.warn("잘못된 JWT 토큰입니다.: {}", url);
                handleForbiddenResponse(httpResponse);
            }

            if (claims.isEmpty()) {
                log.warn("URL에 대한 JWT 토큰이 잘못되었습니다: {}", url);
                handleForbiddenResponse(httpResponse);
            }
            log.info("JWT 검증 성공, 클레임: {}", claims);

        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류 발생: {}", e.getMessage());
            handleForbiddenResponse(httpResponse);
        }
    }

    // 필터 검사에서 걸린 경우
    private void handleForbiddenResponse(HttpServletResponse response) throws IOException {
        // 403 에러
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        // 메세지
        response.getWriter().write("로그인이 되어 있지 않습니다.");
        // 클라이언트로 전송
        response.getWriter().flush();
    }
}
