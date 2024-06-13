package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.LoginResponseDto;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Optional;
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
    @Operation(summary = "Oauth 로그인 성공" , description = "")
    public ApiResponse<LoginResponseDto> loginSuccess(HttpSession session,
                                                      HttpServletResponse response,
                                                      HttpServletRequest request
    ) {
        System.out.println("페이지 호출");

        //세션에서 억세스 토큰 추출
        String accessToken = (String) session.getAttribute("accessToken");
        System.out.println(accessToken);

        // 리스폰스헤더에 억세스토큰 실어서 보내기
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

        System.out.println(accessToken);

        //쿠기에서 리프레쉬 토큰 추출
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieValue(request, "refreshToken");


        //세션에서 억세스 토큰 삭제
        session.removeAttribute("accessToken");

        // 가져온 정보로 dto 생성
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenCookie.get().getValue())
                .build();

            return new ApiResponse<>(HttpStatus.OK, loginResponseDto);
    }

    @GetMapping("/loginFailure")
    public String loginFailure() {
        return "Login Failure!";
    }
}


