package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        System.out.println("인증 성공");

        String accessToken;
        String refreshToken;
        try {
            accessToken = jwtTokenUtil.createToken(user.getId(), "access");
            System.out.println("억세스 토큰 생성 완료 :" + accessToken);
            refreshToken = jwtTokenUtil.createToken(user.getId(), "refresh");
            System.out.println("리프레쉬 토큰 생성 완료 :" + refreshToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        response.setHeader("Authorization","Bearer " + accessToken);
        CookieUtil.createCookie(response, "refreshToken", refreshToken, (int) (JwtTokenUtil.refreshTokenDuration / 1000));


        // onAuthenticationSuccess 가 void값을 반환함으로 APIResponse를 풀어서 직접 보낸다
        response.setStatus(HttpStatus.OK.value());



//        response.sendRedirect("/auth/loginSuccess");
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"accessToken\": \"" + accessToken +
                "\", \"refreshToken\": \"" + refreshToken + "\"}");


    }

}
