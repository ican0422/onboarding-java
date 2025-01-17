package com.careerthon.onboardingjava.common.config;

import com.careerthon.onboardingjava.common.exception.JwtValidationResultException;
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
            handleForbiddenResponse(httpResponse, "JWT 토큰이 없습니다.");
            return;
        }

        // 토큰 검증
        try {
            String token = jwtUtil.substringToken(tokenHeader)
                    .orElseThrow(() -> new JwtValidationResultException("JWT 토큰이 비어 있습니다."));
            Claims claims = jwtUtil.validateAndExtractClaims(token)
                    .orElseThrow(() -> new JwtValidationResultException("Claims 추출 실패했습니다."));

            log.info("JWT 검증 성공, 클레임: {}", claims);
            chain.doFilter(request, response);
        } catch (JwtValidationResultException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            handleForbiddenResponse(httpResponse, e.getMessage());
        } catch (Exception e) {
            log.error("JWT 필터 처리 중 오류 발생: {}", e.getMessage());
            handleForbiddenResponse(httpResponse, "서버 내부 오류가 발생했습니다.");
        }
    }

    // 필터 검사에서 걸린 경우
    private void handleForbiddenResponse(HttpServletResponse response, String message) throws IOException {
        // 403 에러
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        // 메세지
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
        // 클라이언트로 전송
        response.getWriter().flush();
    }
}
