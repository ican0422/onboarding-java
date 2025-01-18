package com.careerthon.onboardingjava.common.domain.user.service;

import com.careerthon.onboardingjava.domain.user.dto.request.UserSignupRequestDto;
import com.careerthon.onboardingjava.domain.user.dto.respons.UserSignupResponseDto;
import com.careerthon.onboardingjava.domain.user.entity.User;
import com.careerthon.onboardingjava.domain.user.entity.UserRole;
import com.careerthon.onboardingjava.domain.user.repository.UserRepository;
import com.careerthon.onboardingjava.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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
        User user = new User(request, UserRole.ROLE_USER);
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
}
