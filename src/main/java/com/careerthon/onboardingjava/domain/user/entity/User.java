package com.careerthon.onboardingjava.domain.user.entity;

import com.careerthon.onboardingjava.domain.user.dto.request.UserSignupRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    @Column(nullable = true, unique = true)
    private Long googleId;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder
    public User(String username, String nickname, String password, UserRole role, Long kakaoId, Long googleId) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.kakaoId = kakaoId;
        this.googleId = googleId;
        this.role = role;
    }
}
