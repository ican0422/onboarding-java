package com.careerthon.onboardingjava.common.domain.user.service;

import com.careerthon.onboardingjava.common.config.JwtUtils;
import com.careerthon.onboardingjava.common.config.PasswordEncoder;
import com.careerthon.onboardingjava.common.config.PasswordEncoderTest;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.request.UserSignupRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignResponseDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignupResponseDto;
import com.careerthon.onboardingjava.domain.user.entity.User;
import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import com.careerthon.onboardingjava.domain.user.repository.UserRepository;
import com.careerthon.onboardingjava.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        JwtUtils jwtUtil = new JwtUtils("dGhpc2lzYXZhbGlkYmFzZTY0ZW5jb2RlZFNlY3JldEtleQ==");
        ReflectionTestUtils.setField(userService, "jwtUtil", jwtUtil);
    }

    @Test
    public void 회원가입_성공() {
        // given
        // dto 값 설정
        String username = "testUsername";
        String password = "testPassword";
        String nickname = "testNickname";

        // dto 생성
        UserSignupRequestDto request = new UserSignupRequestDto(username, password, nickname);

        // 유저 만들기
        long userId = 1L;
        User user = new User(request, password, UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "id", userId);

        // 이미 가입 되어 있는지 확인
        given(userRepository.existsByUsername(username)).willReturn(false);
        // DB 저장
        given(userRepository.save(any())).willReturn(user);

        // when
        UserSignupResponseDto response = userService.signup(request);

        // then
        assertNotNull(response);
        assertEquals(username, response.getUsername());
        assertEquals(nickname, response.getNickname());
        assertEquals(UserRole.ROLE_USER, response.getAuthorities().getAuthorityName());
    }

    @Test
    public void 이미_가입한_유저_예외처리_테스트() {
        // given
        // dto 값 설정
        String username = "testUsername";
        String password = "testPassword";
        String nickname = "testNickname";

        // dto 생성
        UserSignupRequestDto request = new UserSignupRequestDto(username, password, nickname);

        // 유저 만들기
        long userId = 1L;
        User user = new User(request, password, UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "id", userId);

        // 이미 가입 되어 있는지 확인
        given(userRepository.existsByUsername(username)).willReturn(true);

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.signup(request);
        });

        // then
        assertEquals("이미 가입된 회원입니다.", exception.getMessage());
    }

    @Test
    public void 로그인_성공_테스트() {
        // given
        long userId = 1L;
        String username = "testUsername";
        String password = "testPassword";
        String nickname = "testNickname";
        UserRole userRole = UserRole.ROLE_USER;

        // dto 생성
        UserSignRequestDto request = new UserSignRequestDto(username, password);

        // 유저 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "username", request.getUsername());
        ReflectionTestUtils.setField(user, "password", request.getPassword());
        ReflectionTestUtils.setField(user, "nickname", nickname);
        ReflectionTestUtils.setField(user, "role", userRole);

        // 회원 가입 되어 있는지 확인
        given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.of(user));
        // 비밀번호 확인
        given(passwordEncoder.verify(password, request.getPassword())).willReturn(true);

        // when
        UserSignResponseDto response = userService.sign(request);

        // then
        assertNotNull(response);
        System.out.println(response.getToken());
    }

    @Test
    public void 유저를_찾을_수_없는_경우_예외() {
        // given
        String username = "testUsername";
        String password = "testPassword";

        // dto 생성
        UserSignRequestDto request = new UserSignRequestDto(username, password);

        // 유저 네임을 찾을 수 없을 경우
        given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.empty());

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.sign(request);
        });

        // then
        assertEquals("회원 정보를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    public void 비밀번호_불일치_예외() {
        // given
        long userId = 1L;
        String username = "testUsername";
        String password = "testPassword";
        String nickname = "testNickname";
        UserRole userRole = UserRole.ROLE_USER;

        // dto 생성
        UserSignRequestDto request = new UserSignRequestDto(username, password);

        // 유저 생성
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "username", request.getUsername());
        ReflectionTestUtils.setField(user, "password", "password");
        ReflectionTestUtils.setField(user, "nickname", nickname);
        ReflectionTestUtils.setField(user, "role", userRole);

        // 회원 가입 되어 있는지 확인
        given(userRepository.findByUsername(request.getUsername())).willReturn(Optional.of(user));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.sign(request);
        });

        // then
        assertEquals("잘못된 비밀번호 입니다.", exception.getMessage());
    }
}
