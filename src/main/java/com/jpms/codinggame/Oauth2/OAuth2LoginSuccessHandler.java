package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
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

        // 유저의 nickname과 address 확인
        String nickname = user.getNickName();
        String address = user.getAddress();

        HttpSession session = request.getSession();
        session.setAttribute("accessToken", accessToken);
        CookieUtil.createCookie(response, "refreshToken", refreshToken, (int) (JwtTokenUtil.refreshTokenDuration / 1000));

        // 처음 가입이면 추가 정보 기입 페이지로 리다이렉트
        if (nickname == null || nickname.isEmpty() || address == null || address.isEmpty()) {
            response.sendRedirect("/users/add-info");
            return;
        }


        response.sendRedirect("/auth/loginSuccess");



    }

}
