package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.UserLoginResponseDto;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.jpms.codinggame.jwt.CookieUtil.getCookieValue;

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
    public UserLoginResponseDto loginSuccess(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             HttpServletRequest request) {

        if (principalDetails == null) {
            throw new IllegalArgumentException("UserPrincipal is null");
        }

        User user = principalDetails.getUser();

        Optional<Cookie> accessTokenCookie = CookieUtil.getCookieValue(request, "accessToken");
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieValue(request, "refreshToken");

        if (!accessTokenCookie.isPresent() || !refreshTokenCookie.isPresent()) {
            throw new IllegalArgumentException("Access token or refresh token cookie is missing");
        }

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
                .accessToken(accessTokenCookie.get().getValue())
                .refreshToken(refreshTokenCookie.get().getValue())
                .build();
    }

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "Login Failure!";
    }
}


