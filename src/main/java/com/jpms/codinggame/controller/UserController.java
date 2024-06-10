package com.jpms.codinggame.controller;

import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.service.EmailService;
import com.jpms.codinggame.service.TempServerStorage;

import com.jpms.codinggame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name ="계정관리 Controller", description = "계정관리 CRUD API")
public class UserController {

    private final UserService userService;

    private final EmailService emailService;
    private final TempServerStorage tempServerStorage;


    @PostMapping("/signup")
    @Operation(summary = "계정 생성 요청" , description = "")
    public ApiResponse<ResponseDto> signUp(@RequestBody SignupRequestDto signupRequestDto) {
        try {
            userService.signUp(signupRequestDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("회원 가입 완료"));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                    ResponseDto.getInstance("회원 가입 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/find-account")
    @Operation(summary = "아이디 찾기 이메일 요청")
    public ApiResponse<ResponseDto> findAccount(@RequestBody FindUserNameDto findUserNameDto){
        try {
            userService.findAccountName(findUserNameDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("아이디 찾기 이메일 발신 완료"));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                    ResponseDto.getInstance("등록되지 않은 이메일입니다: " + e.getMessage()));
        }
    }

    @PostMapping("/find-password")
    @Operation(summary = "임시 비밀번호 이메일 요청")
    public ApiResponse<ResponseDto> sendTempPassword(@RequestBody FindPasswordDto findPasswordDto){
        try {
            userService.findPassword(findPasswordDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("임시 비밀번호 발신 완료"));
        } catch (Exception e) {
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                    ResponseDto.getInstance("등록되지 않은 이메일입니다: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-email")
    @Operation(summary = "이메일 인증 요청", description = "")
    public ApiResponse<Void> sendVerificationEmail(@RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        int authNum = emailService.sendSignupEmail(emailVerificationRequestDto.getEmail());
        tempServerStorage.saveVerificationCode(emailVerificationRequestDto.getEmail(), authNum);
        return new ApiResponse<>(HttpStatus.OK, null);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 요청" , description = "")
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) throws Exception {
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);

        //access Token
        response.setHeader("Authorization","Bearer " + loginResponseDto.getAccessToken());
        //refresh Token
        CookieUtil.createCookie(
                response,
                "refreshToken",
                loginResponseDto.getRefreshToken(),
                (int) ((JwtTokenUtil.refreshTokenDuration/1000)));

        return "access token : " + loginResponseDto.getAccessToken()
                + "\nrefresh token : " + loginResponseDto.getRefreshToken();
    }

    @GetMapping("/getid")
    @Operation(summary = "유저 id 요청" , description = "")
    public String test(Authentication authentication){
        Long id = (Long) authentication.getPrincipal();
        return (String.valueOf(id));
    }

    @PostMapping("/test")
    public String test() {
        return "success";
    }






}
