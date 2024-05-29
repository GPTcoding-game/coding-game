package com.jpms.codinggame.controller;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.LoginRequestDto;
import com.jpms.codinggame.global.dto.LoginResponseDto;
import com.jpms.codinggame.global.dto.SignupRequestDto;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @PostMapping("/signup")
    public String signUp(@RequestBody SignupRequestDto signupRequestDto) {
        try {
            userService.signUp(signupRequestDto);
            return "회원가입 완료";
        } catch (Exception e) {
            return "회원가입 실패";
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) throws Exception {
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);
        CookieUtil.createCookie(
                response,
                "accessToken",
                loginResponseDto.getAccessToken(),
                (int) ((JwtTokenUtil.accessTokenDuration/1000)));
        CookieUtil.createCookie(
                response,
                "refreshToken",
                loginResponseDto.getRefreshToken(),
                (int) ((JwtTokenUtil.refreshTokenDuration/1000)));

        return "access token : " + loginResponseDto.getAccessToken()
                + "\nrefresh token : " + loginResponseDto.getRefreshToken();
    }

    @GetMapping("/getid")
    public String test(Authentication authentication){
        Long id = (Long) authentication.getPrincipal();
        return (String.valueOf(id));
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }

}
