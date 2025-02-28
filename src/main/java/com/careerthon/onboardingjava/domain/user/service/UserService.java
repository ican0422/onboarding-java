package com.careerthon.onboardingjava.domain.user.service;

import com.careerthon.onboardingjava.common.config.JwtUtils;
import com.careerthon.onboardingjava.common.config.PasswordEncoder;
import com.careerthon.onboardingjava.domain.user.dto.Authorities;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignupRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignResponseDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignupResponseDto;
import com.careerthon.onboardingjava.domain.user.entity.User;
import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import com.careerthon.onboardingjava.domain.user.repository.UserRepository;
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

    // 회원 가입
    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto requestDto) {
        // 이미 가입했는지 확인
        if(userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 엔티티로 변환
        User user = new User(requestDto, encodedPassword, UserRole.ROLE_USER);

        // DB 저장
        User saveUser = userRepository.save(user);

        // UserRole DTO 변환
        Authorities authorities = new Authorities(saveUser.getRole());

        // 리턴
        return new UserSignupResponseDto(saveUser, authorities);
    }

    // 로그인
    @Transactional(readOnly = true)
    public UserSignResponseDto sign(UserSignRequestDto requestDto) {
        // 회원 가입 되어 있는지 확인
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 비밀번호 확인
        boolean verify = passwordEncoder.verify(requestDto.getPassword(), user.getPassword());
        if(!verify) {
            throw new IllegalArgumentException("잘못된 비밀번호 입니다.");
        }

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

        // 토큰 반환
        return new UserSignResponseDto(token, refreshToken);
    }
}
