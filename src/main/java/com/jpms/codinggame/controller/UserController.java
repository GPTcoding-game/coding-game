package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.DeleteUserDto;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import com.jpms.codinggame.service.EmailService;
import com.jpms.codinggame.service.RedisService;
import com.jpms.codinggame.service.SubRedisService;

import com.jpms.codinggame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name ="계정관리 Controller", description = "계정관리 CRUD API")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SubRedisService subRedisService;

    private final RedisService redisService;

    private final JwtTokenUtil jwtTokenUtil;

    

    @PostMapping("/signup")
    @Operation(summary = "계정 생성 요청" , description = "비밀번호 일치 확인 로직은 프론트에서 처리")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 완료 > 로그인 페이지로 이동"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "원인 미상의 회원가입 오류 > 페이지 유지 혹은 다시 로그인 페이지로 이동"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이미 존재하는 계정 > 이미 존재하는 아이디입니다 메시지 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "402", description = "이미 존재하는 닉네임 > 이미 존재하는 닉네임입니다 메시지 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "이미 존재하는 아이디 & 닉네임 > 아이디와 닉네임 입력란에 메시지 동시 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "이메일 인증 실패 > 이메일 인증에 실패하였습니다 메시지 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "405", description = "이미 존재하는 계정 + 이메일 인증 실패 > 아이디 입력란과 이메일 인증칸에 메시지 동시 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "이미 존재하는 닉네임 + 이메일 인증 실패 > 닉네임 입력란과 이메일 인증칸에 메시지 동시 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "407", description = "이미 존재하는 계정 & 닉네임 + 이메일 인증 실패 > 아이디와 닉네임 입력란과 이메일 인증칸에 메시지 동시 출력")

    })
    public ApiResponse<ResponseDto> signUp(@RequestBody SignupRequestDto signupRequestDto) {
            userService.signUp(signupRequestDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("회원 가입 완료"));
    }

    @PostMapping("/find-account")
    @Operation(summary = "아이디 찾기 이메일 요청")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 이메일로 계정이름을 발신 > 로그인 페이지로 이동"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 이메일로 등록되어있는 계정이 없음 > 다른 이메일 입력 필요 메시지 출력")
    })
    public ApiResponse<ResponseDto> findAccount(@RequestBody FindUserNameDto findUserNameDto){
            userService.findAccountName(findUserNameDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("아이디 찾기 이메일 발신 완료"));
    }

    @PostMapping("/find-password")
    @Operation(summary = "임시 비밀번호 이메일 요청")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 이메일로 임시 비밀번호를 발신 > 로그인 페이지로 이동"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 이메일로 등록되어있는 계정이 없음 > 다른 이메일 입력 필요 메시지 출력")
    })
    public ApiResponse<ResponseDto> sendTempPassword(@RequestBody FindPasswordDto findPasswordDto){
            userService.findPassword(findPasswordDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("임시 비밀번호 발신 완료"));
    }

    @PostMapping("/verify-email")
    @Operation(summary = "이메일 인증 요청", description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 이메일로 인증번호를 발신 > 로그인 페이지로 이동"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 이메일로 등록되어있는 계정이 없음 > 다른 이메일 입력 필요 메시지 출력")
    })
    public ApiResponse<Void> sendVerificationEmail(@RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        int authNum = emailService.sendSignupEmail(emailVerificationRequestDto.getEmail());
        subRedisService.setValue(emailVerificationRequestDto.getEmail(), String.valueOf(authNum));
        return new ApiResponse<>(HttpStatus.OK, null);
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인 요청" , description = "비밀번호 일치 확인 로직은 프론트에서 처리")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "accessToken: 엑세스 토큰 , refreshToken: 리프레쉬 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "비밀번호가 계정과 일치하지않음 > 비밀번호를 확인하세요 메시지 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 계정으로 가입되어있는 회원이 없음 > 아이디를 확인하세요 메시지 출력"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "유저id 암호화 실패 > 다시 로그인 시도 필요")
    })
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = userService.login(loginRequestDto);

        //access Token
        response.setHeader("Authorization","Bearer " + loginResponseDto.getAccessToken());
        //refresh Token
        CookieUtil.createCookie(
                response,
                "refreshToken",
                loginResponseDto.getRefreshToken(),
                (int) ((JwtTokenUtil.refreshTokenDuration/1000)));

        return new ApiResponse<>(HttpStatus.OK, loginResponseDto);
    }

//    @GetMapping("/getid")
//    @Operation(summary = "유저 id 요청" , description = "")
//    public String test(Authentication authentication){
//        Long id = (Long) authentication.getPrincipal();
//        return (String.valueOf(id));
//    }


    /**서버에서 Oauth2 인증을 사용할 경우 사용되는 필수 정보 입력 컨트롤러 (최종본에는 SDK 인증방식으로 사용하여 사용하지 않음)**/
//    @GetMapping("/add-info")
//    @Operation(summary = "이미 입력되어 있는 정보 요청", description = "")
//    public ApiResponse<GetInfoResponseDto> getExistingInfo (HttpSession session
////                                                            , HttpServletResponse response
////                                                            , HttpServletRequest request
//
//    ) {
//        Long userId = Long.valueOf((String) session.getAttribute("userId"));
//        Optional<User> optionalUser = userRepository.findById(userId);
//        if(optionalUser.isEmpty()){throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);}
//
//        return  new ApiResponse<>(HttpStatus.OK,userService.getCompulsoryInfo(optionalUser.get()));
//    }
//
//    //닉네임 필드가 비어있거나 중복 되었을 경우 예외처리
//    @PutMapping("/add-info")
//    @Operation(summary = "추가 정보 입력", description = "")
//    public ApiResponse<LoginResponseDto> addInfo(@ RequestBody NicknameAddressDto addInfoDto,
//                                                 HttpSession session,
//                                                 HttpServletResponse response,
//                                                 HttpServletRequest request)
//    {
//        String accessToken = (String) session.getAttribute("accessToken");
//        Long userId = Long.valueOf((String) session.getAttribute("userId"));
//        Optional<User> optionalUser = userRepository.findById(userId);
//        if(optionalUser.isEmpty()){throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);}
//        User user = optionalUser.get();
//
//        userService.addOauthUserInfo(addInfoDto, user);
//
//        // 리스폰스헤더에 억세스토큰 실어서 보내기
//        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
//        //레디스에서 리프레쉬 토큰 추출
//        String refreshToken = (String) redisService.get(String.valueOf(userId), "refreshToken");
//
//        // 쿠키 생성
//        CookieUtil.createCookie(
//                response,
//                "refreshToken",
//                refreshToken,
//                (int) ((JwtTokenUtil.refreshTokenDuration/1000)));
//
//
//        //세션에서 억세스 토큰과 유저정보 삭제
//        session.removeAttribute("accessToken");
//        session.removeAttribute("userId");
//
//        // 가져온 정보로 dto 생성
//        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//
//        return new ApiResponse<>(HttpStatus.OK, loginResponseDto);
//    }


    @PostMapping("/logout")
    @Operation(summary = "로그 아웃 실행", description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공적인 로그아웃 > 로그인 페이지 혹은 메인페이지로 리다이렉트 필요")
    })
    public ApiResponse<ResponseDto> logOut(HttpSession session,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        userService.logOut(session, request, response);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("로그아웃 되었습니다."));
    }


//    @PostMapping("/redis/test")
//    public String testRedis(@RequestBody EmailVerificationRequestDto dto) {
//        if(subRedisService.getValue(dto.getEmail())== null) return "null값임";
//        return subRedisService.getValue(dto.getEmail());}

    @DeleteMapping("/delete")
    @Operation(summary = "회원탈퇴 로직", description = "정말로 탈퇴하시겠습니까? 버튼 프론트에서 생성 필요")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공적으로 탈퇴완료 > 로그인 페이지 혹은 메인페이지로 리다이렉트 필요")
    })
    public ApiResponse<ResponseDto> deleteUser(Authentication authentication){
        userService.deleteUser(authentication);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("유저삭제 완료."));
    }

    // 인증 없이 스웨거로 쉽게 유저삭제 테스트를 위한 컨트롤러
    @DeleteMapping("/swagger-delete")
    public ApiResponse<ResponseDto> deleteUser(@RequestBody DeleteUserDto deleteUserDto){
        userService.deleteUserWithSwagger(deleteUserDto);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("유저삭제 완료."));
    }



}
