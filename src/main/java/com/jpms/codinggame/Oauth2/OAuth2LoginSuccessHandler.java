package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import com.jpms.codinggame.service.RedisService;
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

    private final RedisService redisService;

    public OAuth2LoginSuccessHandler(
            JwtTokenUtil jwtTokenUtil
            , UserRepository userRepository,
            RedisService redisService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        System.out.println("인증 성공");

        String accessToken = jwtTokenUtil.createToken(user.getId(), "access");
//            System.out.println("억세스 토큰 생성 완료 :" + accessToken);
        String refreshToken = jwtTokenUtil.createToken(user.getId(), "refresh");
//            System.out.println("리프레쉬 토큰 생성 완료 :" + refreshToken);


        // 유저의 nickname과 address 확인
        String nickname = user.getNickName();
        String address = user.getAddress();
        String email = user.getEmail();

        HttpSession session = request.getSession();
        session.setAttribute("userId", String.valueOf(user.getId()));
        session.setAttribute("accessToken", accessToken);

        // refreshToken을 레디스에 저장
        redisService.put(String.valueOf(user.getId()), "refreshToken", refreshToken);

//        CookieUtil.createCookie(response, "refreshToken", refreshToken, (int) (JwtTokenUtil.refreshTokenDuration / 1000));

        // 처음 가입이면 추가 정보 기입 페이지로 리다이렉트
        if (nickname == null || nickname.isEmpty()
                || address == null || address.isEmpty()
                || email == "" || email.isEmpty())
        {
//            response.sendRedirect("ioscodinggame://auth?code=needinfo");
            System.out.println("소셜 로그인 완료: 추가 정보 기입 필요");
            response.sendRedirect("/users/add-info");
        }
        else{
            //response.sendRedirect("ioscodinggame://auth?code=complete");
            response.sendRedirect("/auth/loginSuccess");
        }







    }

}
