package com.jpms.codinggame.controller;

import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.DeleteUserDto;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.exception.ValidationErrorCode;
import com.jpms.codinggame.exception.ValidationException;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import com.jpms.codinggame.service.EmailService;
import com.jpms.codinggame.service.SubRedisService;

import com.jpms.codinggame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    private final JwtTokenUtil jwtTokenUtil;




    @PostMapping("/signup")
    @Operation(summary = "계정 생성 요청" , description = "")
    public ApiResponse<ResponseDto> signUp(@RequestBody SignupRequestDto signupRequestDto) {
//        try {
            userService.signUp(signupRequestDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("회원 가입 완료"));
//        }  catch (ValidationException e) {
//            List<String> errorMessages = e.getErrorCodes().stream()
//                    .map(ValidationErrorCode::getMessage)
//                    .collect(Collectors.toList());
//            return new ApiResponse<>(HttpStatus.BAD_REQUEST, ResponseDto.getInstance("회원 가입 실패: " + String.join(", ", errorMessages)));
//        }
    }

    @PostMapping("/find-account")
    @Operation(summary = "아이디 찾기 이메일 요청")
    public ApiResponse<ResponseDto> findAccount(@RequestBody FindUserNameDto findUserNameDto){
//        try {
            userService.findAccountName(findUserNameDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("아이디 찾기 이메일 발신 완료"));
//        } catch (CustomException e) {
//            return new ApiResponse<>(HttpStatus.BAD_REQUEST,
//                    ResponseDto.getInstance("등록되지 않은 이메일입니다: " + e.getMessage()));
//        }
    }

    @PostMapping("/find-password")
    @Operation(summary = "임시 비밀번호 이메일 요청")
    public ApiResponse<ResponseDto> sendTempPassword(@RequestBody FindPasswordDto findPasswordDto){
//        try {
            userService.findPassword(findPasswordDto);
            return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("임시 비밀번호 발신 완료"));
//        } catch (Exception e) {
//            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
//                    ResponseDto.getInstance("등록되지 않은 이메일입니다: " + e.getMessage()));
//        }
    }

    @PostMapping("/verify-email")
    @Operation(summary = "이메일 인증 요청", description = "")
    public ApiResponse<Void> sendVerificationEmail(@RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        int authNum = emailService.sendSignupEmail(emailVerificationRequestDto.getEmail());
        subRedisService.setValue(emailVerificationRequestDto.getEmail(), String.valueOf(authNum));
//        tempServerStorage.saveVerificationCode(emailVerificationRequestDto.getEmail(), authNum);
        return new ApiResponse<>(HttpStatus.OK, null);
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인 요청" , description = "")
    public ApiResponse<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) throws Exception {
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

    @GetMapping("/getid")
    @Operation(summary = "유저 id 요청" , description = "")
    public String test(Authentication authentication){
        Long id = (Long) authentication.getPrincipal();
        return (String.valueOf(id));
    }


    // 소셜로그인 추가정보를 하고 리스폰스에 바로 토큰을 부여할것인지 아니면 그냥 넘겨버릴껀지 고민해야됨
    @GetMapping("/add-info")
    @Operation(summary = "이미 입력되어 있는 정보 요청", description = "")
    public ApiResponse<GetInfoResponseDto> getExistingInfo (HttpSession session
//                                                            , HttpServletResponse response
//                                                            , HttpServletRequest request

    ) {
        Long userId = Long.valueOf((String) session.getAttribute("userId"));
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);}

        return  new ApiResponse<>(HttpStatus.OK,userService.getCompulsoryInfo(optionalUser.get()));
    }

    //닉네임 필드가 비어있거나 중복 되었을 경우 예외처리
    @PutMapping("/add-info")
    @Operation(summary = "추가 정보 입력", description = "")
    public ApiResponse<LoginResponseDto> addInfo(@ RequestBody AddInfoDto addInfoDto,
                                                 HttpSession session,
                                                 HttpServletResponse response,
                                                 HttpServletRequest request)
    {
        String accessToken = (String) session.getAttribute("accessToken");
        Long userId = Long.valueOf((String) session.getAttribute("userId"));
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);}
        User user = optionalUser.get();

        userService.addOauthUserInfo(addInfoDto, user);

        // 리스폰스헤더에 억세스토큰 실어서 보내기
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        //쿠기에서 리프레쉬 토큰 추출
        Optional<Cookie> refreshTokenCookie = CookieUtil.getCookieValue(request, "refreshToken");


        //세션에서 억세스 토큰과 유저정보 삭제
        session.removeAttribute("accessToken");
        session.removeAttribute("userId");

        // 가져온 정보로 dto 생성
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenCookie.get().getValue())
                .build();

        return new ApiResponse<>(HttpStatus.OK, loginResponseDto);
    }


    @PostMapping("/logout")
    @Operation(summary = "로그 아웃 실행", description = "")
    public ApiResponse<ResponseDto> logOut(Authentication authentication,
                                           HttpSession session,
                                           HttpServletRequest request,
                                           HttpServletResponse response) {
        userService.logOut(authentication, session, request, response);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("로그아웃 되었습니다."));
    }


    @PostMapping("/test")
    public String test() {
        return "success";
    }


    @PostMapping("/redis/test")
    public String testRedis(@RequestBody EmailVerificationRequestDto dto) {
        if(subRedisService.getValue(dto.getEmail())== null) return "null값임";
        return subRedisService.getValue(dto.getEmail());}

    @DeleteMapping("/delete")
    public ApiResponse<ResponseDto> deleteUser(Authentication authentication){
        userService.deleteUser(authentication);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("유저삭제 완료."));
    }

    @DeleteMapping("/swagger-delete")
    public ApiResponse<ResponseDto> deleteUser(DeleteUserDto deleteUserDto){
        userService.deleteUserWithSwagger(deleteUserDto);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("유저삭제 완료."));
    }


}
