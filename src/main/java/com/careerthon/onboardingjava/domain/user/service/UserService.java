package com.careerthon.onboardingjava.domain.user.service;

import com.careerthon.onboardingjava.common.config.JwtUtils;
import com.careerthon.onboardingjava.common.config.PasswordEncoder;
import com.careerthon.onboardingjava.domain.user.dto.Authorities;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignupRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.KakaoUserInfoDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.KakaoUserResponse;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignResponseDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignupResponseDto;
import com.careerthon.onboardingjava.domain.user.entity.User;
import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import com.careerthon.onboardingjava.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;

    /* 회원 가입 */
    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto requestDto) {
        // 이미 가입했는지 확인
        if(userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 엔티티로 변환
        User user = User.builder()
                .username(requestDto.getUsername())
                .nickname(requestDto.getNickname())
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .build();

        // DB 저장
        User saveUser = userRepository.save(user);

        // UserRole DTO 변환
        Authorities authorities = new Authorities(saveUser.getRole());

        // 리턴
        return new UserSignupResponseDto(saveUser, authorities);
    }

    /* 로그인 */
    @Transactional(readOnly = true)
    public UserSignResponseDto sign(UserSignRequestDto requestDto, HttpServletResponse response) {
        // 회원 가입 되어 있는지 확인
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 비밀번호 확인
        boolean verify = passwordEncoder.verify(requestDto.getPassword(), user.getPassword());
        if(!verify) {
            throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
        }

        // 토큰 발행
        String token = tokenOrRefreshToken(user, response);

        // 토큰 반환
        return new UserSignResponseDto(token);
    }

    /* 카카오 로그인 or 회원가입 (JWT 토큰 반환 및 리프레시 토큰 저장) */
    @Transactional
    public KakaoUserResponse kakaoSignupOrSign(KakaoUserInfoDto kakaoUserInfoDto, HttpServletResponse response) {
        // 카카오 ID로 가입 된 유저인지 확인, 가입 되어 있지 않으면 회원가입
        User user = userRepository.findByKakaoId(kakaoUserInfoDto.getId())
                .orElseGet(() -> kakaoSaveUser(kakaoUserInfoDto));

        // UserRole DTO 변환
        Authorities authorities = new Authorities(user.getRole());

        // 토큰 발행 및 리프레시 토큰 발행 및 쿠키 저장
        String token = tokenOrRefreshToken(user, response);

        return new KakaoUserResponse(user, authorities, token);
    }

    /* 카카오 회원가입 */
    private User kakaoSaveUser(KakaoUserInfoDto kakaoUserInfoDto) {
        // 유저네임
        String username = "kakao_" + kakaoUserInfoDto.getId();
        // 닉네임
        String nickname = kakaoUserInfoDto.getNickname();
        // 비밀번혼
        String password = passwordEncoder.encode("kakao_" + kakaoUserInfoDto.getId() + "_" + nickname);
        // 역할
        UserRole role = UserRole.ROLE_USER;

        // 엔티티 주입
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .kakaoId(kakaoUserInfoDto.getId())
                .role(role)
                .build();

        // 유저 저장
        return userRepository.save(user);
    }

    /* 토큰 발행 및 리프레시 토큰 발행 및 쿠키 저장 */
    private String tokenOrRefreshToken(User user, HttpServletResponse response) {
        // 토큰 발행
        String token = jwtUtil.createToken(user.getId(), user.getUsername(), user.getNickname(), user.getRole());
        // 리프레시 토큰 발행
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getUsername(), user.getNickname(), user.getRole());

        // 리프레시 토큰 redis에 저장
        redisTemplate.opsForValue().set(
                "refresh_token:" + user.getId(),
                refreshToken,
                jwtUtil.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS);

        // 리프레시 토큰 쿠키에 저장
        int refreshTokenTime = Math.toIntExact(jwtUtil.getRefreshTokenExpirationTime());
        Cookie refreshTokenCookie = seveRefreshTokenCookie(refreshToken, refreshTokenTime);
        response.addCookie(refreshTokenCookie);  // 클라이언트에 전송

        return token;
    }

    /* 리프레시 토큰 쿠키 저장 */
    private Cookie seveRefreshTokenCookie(String refreshToken, int refreshTokenTime) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);               // XSS 공격 방지
        refreshTokenCookie.setSecure(true);                 // HTTPS에서만 전송
        refreshTokenCookie.setPath("/auth/tokens");         // 해당 경로에서만 사용 가능
        refreshTokenCookie.setMaxAge(refreshTokenTime);     // 토큰 일수 int 변환

        return refreshTokenCookie;
    }
}
