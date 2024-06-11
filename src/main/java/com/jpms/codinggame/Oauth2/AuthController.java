package com.jpms.codinggame.Oauth2;

import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.LoginResponseDto;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    public ApiResponse<LoginResponseDto> loginSuccess(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                      HttpServletRequest request) {

        String accessToken = null;
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.split(" ")[1];
        }

        System.out.println(accessToken);

//        User user = principalDetails.getUser();
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieValue(request, "refreshToken");

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


