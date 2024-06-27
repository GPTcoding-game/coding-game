package com.jpms.codinggame.SDKSocialLogin;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.jwt.CookieUtil;
import com.jpms.codinggame.jwt.JwtTokenUtil;
import com.jpms.codinggame.repository.UserRepository;
import com.jpms.codinggame.service.RedisService;
import com.jpms.codinggame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.security.GeneralSecurityException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name ="SDK 기반 소셜로그인 Controller", description = "SDK 소셜 로그인 API")
public class SDKController {

    private final TokenVerifier tokenVerifier;
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    private final RedisService redisService;

    @PostMapping("/{provider}")
    @Operation(summary = "소셜로그인 처리 컨트롤러", description = "제공자를 파악 한 후 알맞은 메소드 실행")
    public void socialLogin(
            @PathVariable("provider") String provider,
            @RequestBody SocialLoginRequestDto socialLoginRequestDto,
            HttpServletResponse response,
            HttpSession session
    ) throws GeneralSecurityException, IOException, InterruptedException {
        String email = null;
        String username = null;

        switch (provider) {
            case "google":
                GoogleIdToken.Payload payload = tokenVerifier.verifyGoogleToken(socialLoginRequestDto.getToken());
                email = payload.getEmail();
                username = (String) payload.get("name");
                break;

            case "kakao":
                JsonNode kakaoPayload = tokenVerifier.verifyKakaoToken(socialLoginRequestDto.getToken());
                email = kakaoPayload.get("kakao_account").get("email").asText();
                // 지금 카카오 닉네임을 유저네임으로 받아오는데 해당필드는 유니크 필드임 카카오 닉네임 중복에 대한 처리 혹은 유저객체 필드 조건의 수정필요
                username = kakaoPayload.get("kakao_account").get("profile").get("nickname").asText();
                break;

            case "apple":
                // 애플 소셜로그인 처리 로직
                break;

            default:
                throw new CustomException(ErrorCode.INVALID_PROVIDER);
        }
        // 지금은 여기서 유저객체를 저장하지만, dto에 값을 넣어서 전달하고 추가정보 입력이 완료된 후 유저객체를 생성할 수 있을 것으로 보임
        Long userId = userService.socialSignup(email, username);
        session.setAttribute("userId", userId.toString());

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        }

        User user = optionalUser.get();
        String nickname = user.getNickName();
        String address = user.getAddress();
        email = user.getEmail();

        // 처음 가입이면 추가 정보 기입 필요
        if (nickname == null || nickname.isEmpty()
                || address == null || address.isEmpty()
                || email == null || email.isEmpty()) {
            response.sendRedirect("/auth/add-info");
        } else {
            response.sendRedirect("/auth/loginSuccess");
        }
    }


    @GetMapping("/add-info")
    @Operation(summary = "이미 입력되어 있는 정보 요청", description = "")
    public ApiResponse<GetInfoResponseDto> getExistingInfo (
            HttpSession session
    ) {
        Long userId = Long.valueOf((String) session.getAttribute("userId"));
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);}

        return  new ApiResponse<>(HttpStatus.OK,userService.getCompulsoryInfo(optionalUser.get()));
    }

    //닉네임 필드가 비어있거나 중복 되었을 경우 예외처리
    @PutMapping("/add-info")
    @Operation(summary = "추가 정보 입력", description = "")
    public void addInfo(
            @RequestBody AddInfoDto addInfoDto,
            HttpSession session,
            HttpServletResponse response
    ) throws IOException {
        Long userId = Long.valueOf((String) session.getAttribute("userId"));
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()){throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);}
        User user = optionalUser.get();

        userService.addOauthUserInfo(addInfoDto, user);
        response.sendRedirect("/auth/loginSuccess");
    }


    @GetMapping("/loginSuccess")
    @Operation(summary = "소셜 로그인 성공" , description = "")
    public ApiResponse<LoginResponseDto> loginSuccess(
            HttpSession session,
            HttpServletResponse response,
            HttpServletRequest request
    ) {

        Long userId = Long.valueOf((String) session.getAttribute("userId"));

        // 엑세스 토큰 생성
        String accessToken = jwtTokenUtil.createToken(userId, "access");

        // 리프레쉬 토큰 생성
        String refreshToken = jwtTokenUtil.createToken(userId, "refresh");;

        // 리프레쉬 토큰을 레디스에 저장
        redisService.put(String.valueOf(userId), "refreshToken", refreshToken);

        // 엑세스 토큰을 헤더에 싣는다
        response.setHeader("Authorization","Bearer " + accessToken);

        // 리프레쉬 토큰으로 쿠키 생성
        CookieUtil.createCookie(response, "refreshToken", refreshToken, (int) (JwtTokenUtil.refreshTokenDuration / 1000));

        // 세션에서 유저id 제거
        session.removeAttribute("userId");

        // 시큐리티 컨택스트에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(jwtTokenUtil.getAuthentication(userId));


        // 가져온 정보로 dto 생성
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return new ApiResponse<>(HttpStatus.OK, loginResponseDto);
    }
}
