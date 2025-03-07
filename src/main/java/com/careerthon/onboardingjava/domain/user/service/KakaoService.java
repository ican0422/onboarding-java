package com.careerthon.onboardingjava.domain.user.service;

import com.careerthon.onboardingjava.domain.user.dto.respons.KakaoUserDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class KakaoService {
    private final RestTemplate restTemplate;
    private final String clientId;
    private final String redirectUri;

    public KakaoService(
            @Value("${kakao.client_id}") String clientId,
            @Value("${kakao.redirect_uri}") String redirectUri,
            RestTemplateBuilder builder
    ) {
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.restTemplate = builder.build();
    }

    /* 카카오 엑세스 토큰 요청 */
    public String getAccessToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 응답을 JSON 문자열로 받음
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            return rootNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("카카오 액세스 토큰 파싱 중 오류 발생", e);
        }
    }

    /* 카카오 사용자 정보 요청 (닉네임만 가져오기) */
    public KakaoUserDto getKakaoUser(String token) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            Long id = rootNode.get("id").asLong();
            String nickname = rootNode.get("properties").get("nickname").asText();

            return new KakaoUserDto(id, nickname);
        } catch (Exception e) {
            throw new RuntimeException("카카오 API 요청 중 오류가 발생했습니다.", e);
        }
    }
}
