package com.careerthon.onboardingjava.domain.user.entity;

import com.careerthon.onboardingjava.domain.user.dto.request.UserSignupRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String nickname;

    @Column(nullable = true, unique = true)
    private Long kakaoId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User(UserSignupRequestDto dto, String password, UserRole role) {
        this.username = dto.getUsername();
        this.password = password;
        this.nickname = dto.getNickname();
        this.role = role;
    }

    public void kakaoUserId(Long kakaoId) {
        this.kakaoId = kakaoId;
    }
}
