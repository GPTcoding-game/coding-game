package com.jpms.codinggame.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Optional<Cookie> accessTokenCookie = CookieUtil.getCookieValue(request, "accessToken");
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieValue(request, "refreshToken");

        // access 토큰이 있는 경우
        if (accessTokenCookie.isPresent()) {
            String accessToken = accessTokenCookie.get().getValue();

            // access 토큰 유효성 검사
            if (jwtTokenUtil.validateToken(accessToken)) {
                long userId;
                try {
                    userId = jwtTokenUtil.getId(accessToken);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // 사용자 인증 설정
                SecurityContextHolder.getContext().setAuthentication(
                        jwtTokenUtil.getAuthentication(userId)
                );

                filterChain.doFilter(request, response);
                return;
            }
        }

        // access 토큰이 없는 경우 또는 만료된 경우
        if (refreshTokenCookie.isPresent()) {
            String refreshToken = refreshTokenCookie.get().getValue();

            // refresh 토큰 유효성 검사
            if (jwtTokenUtil.validateToken(refreshToken)) {
                long userId;
                try {
                    userId = jwtTokenUtil.getId(refreshToken);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                // access 토큰 발급
                String newAccessToken;
                try {
                    newAccessToken = jwtTokenUtil.createToken(userId, "access");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                CookieUtil.createCookie(response, "accessToken", newAccessToken,
                        (int) jwtTokenUtil.accessTokenDuration / 1000);

                SecurityContextHolder.getContext().setAuthentication(
                        jwtTokenUtil.getAuthentication(userId)
                );

                filterChain.doFilter(request, response);
                return;
            }
        }

        // access 토큰 및 refresh 토큰 모두 없는 경우 또는 refresh 토큰이 만료된 경우
        filterChain.doFilter(request, response);
    }
}
