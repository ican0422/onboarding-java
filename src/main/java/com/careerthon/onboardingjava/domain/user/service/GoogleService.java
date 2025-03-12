package com.careerthon.onboardingjava.domain.user.service;

import com.careerthon.onboardingjava.domain.user.dto.respons.GoogleUserInfoDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.KakaoUserInfoDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class GoogleService {
    private final RestTemplate restTemplate;
    private final String googleClientId;
    private final String googleClientSecret;
    private final String googleRedirectUri;

    public GoogleService(
            @Value("${google.auth.client}") String googleClientId,
            @Value("${google.auth.client-secret}") String googleClientSecret,
            @Value("${google.auth.redirect}") String googleRedirectUri,
            RestTemplateBuilder builder
    ) {
        this.googleClientId = googleClientId;
        this.googleClientSecret = googleClientSecret;
        this.googleRedirectUri = googleRedirectUri;
        this.restTemplate = builder.build();
    }

    /* 구글 엑세스 토큰 요청 */
    public String getAccessToken(String code) {
        String url = "https://oauth2.googleapis.com/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 응답을 JSON 문자열로 받음
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            return rootNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("구글 액세스 토큰 파싱 중 오류 발생", e);
        }
    }

    /* 구글 사용자 정보 요청 (닉네임만 가져오기) */
    public GoogleUserInfoDto getGoogleUser(String accessToken) {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            Long id = Long.parseLong(rootNode.get("sub").toString());
            String name = String.valueOf(rootNode.get("name"));

            return new GoogleUserInfoDto(id, name);
        } catch (Exception e) {
            throw new RuntimeException("구글 API 요청 중 오류가 발생했습니다.", e);
        }
    }
}
