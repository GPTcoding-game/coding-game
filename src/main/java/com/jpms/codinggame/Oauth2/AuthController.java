package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.UserLoginResponseDto;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")

public class AuthController {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public AuthController(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/loginSuccess")
    public UserLoginResponseDto loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) throws Exception {
        if (oAuth2User == null) {
            throw new IllegalArgumentException("OAuth2User is null");
        }

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) oAuth2User;
        User user = customOAuth2User.getUser();

        String accessToken = jwtTokenUtil.createToken(user.getId(), "access");
        String refreshToken = jwtTokenUtil.createToken(user.getId(), "refresh");

        return UserLoginResponseDto.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .tier(user.getTier())
                .score(user.getScore())
                .isDone(user.isDone())
                .role(user.getRole())
                .address(user.getAddress())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "Login Failure!";
    }
}


