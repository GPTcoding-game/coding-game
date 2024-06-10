package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;

    public OAuth2LoginSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
//        System.out.println("인증 성공");

        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtTokenUtil.createToken(user.getId(), "access");
//            System.out.println("토큰 생성 완료");
            refreshToken = jwtTokenUtil.createToken(user.getId(), "refresh");
//            System.out.println("토큰 생성 완료");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CookieUtil.createCookie(response, "accessToken", accessToken, (int) (JwtTokenUtil.accessTokenDuration / 1000));
        CookieUtil.createCookie(response, "refreshToken", refreshToken, (int) (JwtTokenUtil.refreshTokenDuration / 1000));

        response.sendRedirect("/auth/loginSuccess");
    }

}
