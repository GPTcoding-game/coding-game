package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;

    public OAuth2LoginSuccessHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        CustomOAuth2User customOAuth2User = (CustomOAuth2User) oAuth2User;
        User user = customOAuth2User.getUser();

        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtTokenUtil.createToken(user.getId(), "access");
            refreshToken = jwtTokenUtil.createToken(user.getId(), "refresh");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        CookieUtil.createCookie(response, "accessToken", accessToken, (int) (JwtTokenUtil.accessTokenDuration / 1000));
        CookieUtil.createCookie(response, "refreshToken", refreshToken, (int) (JwtTokenUtil.refreshTokenDuration / 1000));


        request.getSession().setAttribute("oauth2User", customOAuth2User);

        response.sendRedirect("/auth/loginSuccess");
    }

}
