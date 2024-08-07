package com.jpms.codinggame.jwt;

import com.jpms.codinggame.config.PermitAllEndpoint;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.service.RedisService;
import com.jpms.codinggame.service.UserService;
import jakarta.mail.Address;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    private final RedisService redisService;

    private final UserService userService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("필터 진입");



        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieValue(request, "refreshToken");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 인증이 필요없는 요청인지 확인
            if (PermitAllEndpoint.isPermitAll(request.getRequestURI())) {
                System.out.println("인증이 필요없는 요청");
                filterChain.doFilter(request, response);
                return;
            } else {
                // 인증이 필요한 요청인데 Authorization 헤더가 없는 경우 예외 발생
                // 로그인에 필요한 예외처리 발생 만들기
                System.out.println("인증이 필요한 요청에 헤더가 없음");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "");
                return;
            }
        }
        Long userId;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.split(" ")[1];
            // access 토큰 유효성 검사
            if (jwtTokenUtil.validateToken(accessToken)) {
                userId = jwtTokenUtil.getId(accessToken, request.getSession(), request, response);
                // 사용자 인증 설정
                SecurityContextHolder.getContext().setAuthentication(jwtTokenUtil.getAuthentication(userId));
                filterChain.doFilter(request, response);
                return;
            }
        }

        // access 토큰이 없거나 만료된 경우
        if (refreshTokenCookie.isPresent()) {
            String refreshToken = refreshTokenCookie.get().getValue();
            userId = jwtTokenUtil.getId(refreshToken, request.getSession(), request, response);
            String redisRefreshToken = (String) redisService.get(String.valueOf(userId), "refreshToken");

            // redis의 토큰과 대조
            if(!refreshToken.equals(redisRefreshToken)) throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);

            // refresh 토큰 유효성 검사
            if (jwtTokenUtil.validateToken(refreshToken)) {


                // access 토큰 발급
                String newAccessToken = jwtTokenUtil.createToken(userId, "access");;

                // 새로 발급한 access 토큰을 헤더에 추가
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken);
                System.out.println("New Access Token: " + newAccessToken);

                // 사용자 인증 설정
                SecurityContextHolder.getContext().setAuthentication(jwtTokenUtil.getAuthentication(userId));
                filterChain.doFilter(request, response);
                return;
            }
        }

        // 모두 만료시 로그아웃
        throw new CustomException(ErrorCode.TOKEN_EXPIRED);
//        userService.logOut(request.getSession(), request, response);
//        filterChain.doFilter(request, response);
    }
}
