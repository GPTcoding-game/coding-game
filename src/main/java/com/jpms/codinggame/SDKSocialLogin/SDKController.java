package com.jpms.codinggame.SDKSocialLogin;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.jpms.codinggame.entity.Role;
import com.jpms.codinggame.entity.Tier;
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
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name ="SDK 기반 소셜로그인 Controller", description = "{provider}의 요청이 통과 되었을 시 /add-info 로 자동 리다이렉트 \n"+
                                                "/add-info의 put/post 요청이 통과 되었을 시 /loginsuccess 로 자동 리다이렉트")
public class SDKController {

    private final SDKService sdkService;
//    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RedisService redisService;

    @PostMapping("/{provider}")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이메일로 이미 가입한 회원이 있을때 > 다른 이메일이 등록된 소셜 계정 사용해야함"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "소셜 로그인 제공자가 잘못되었을 때 > 다시 로그인 시도 필요")
    })
    @Operation(summary = "소셜로그인 처리 컨트롤러", description = "제공자를 파악 한 후 알맞은 메소드 실행")
    public void socialLogin(
            @PathVariable("provider") String provider,
            @RequestBody SocialLoginRequestDto socialLoginRequestDto,
            HttpServletResponse response,
            HttpSession session
    ) throws Exception {
        System.out.println("소셜로그인 실행");
        String email = null;
        // 제공자 페이로드에서 계정이름 추출 시 중복의 우려가 있음으로 임의로 설정한다
        long count = userRepository.countByProvider(provider) + 1;
        String username = provider+"User"+count;

        switch (provider) {
            case "google":
                GoogleIdToken.Payload payload = sdkService.verifyGoogleToken(socialLoginRequestDto.getToken());
                email = payload.getEmail();
//                username = (String) payload.get("name");
                break;

            case "kakao":
                JsonNode kakaoPayload = sdkService.verifyKakaoToken(socialLoginRequestDto.getToken());
                email = kakaoPayload.get("kakao_account").get("email").asText();

//                username = kakaoPayload.get("kakao_account").get("profile").get("nickname").asText();
                break;

            case "apple":
                Map<String, Object> applePayload = sdkService.verifyAppleToken(socialLoginRequestDto.getToken());
                email = (String) applePayload.get("email");
                break;


            default:
                throw new CustomException(ErrorCode.INVALID_PROVIDER);
        }


        // 추출된 이메일에 등록된 유저 확인
        Optional<User> optionalUser =userRepository.findByEmail(email);

        // 신규 회원이라면
        if (optionalUser.isEmpty()){
            // 신구 회원가입을 위한 필드 임시저장
            session.setAttribute("email", email);
            session.setAttribute("username", username);
            session.setAttribute("provider", provider);
            // 추가적으로 닉네임을 세션에 저장해두고 닉네임 입력시에 자동으로 자신의 소셜계정 닉네임을 받아오는 것 고려

            // 추가 정보 입력으로 디라이렉트
            response.sendRedirect("/auth/add-info");
        }
        // 기존 회원이라면
        else {
            User user = optionalUser.get();

            // 해당 이메일로 가입이되어있는 다른제공자의 계정이 존재
            if (!user.getProvider().equals(provider)){
                throw new CustomException(ErrorCode.EXISTING_EMAIL_EXCEPTION, "(" + user.getProvider() + ")");
            }
            // 세션에 유저id 세팅
            session.setAttribute("userId", user.getId());

            //기존 유저의 필수정보에 손상이 있는경우
            if(!sdkService.checkCompulsoryField(user)){
                response.sendRedirect("/auth/add-info");
            } else{
                // 조건을 모두 만족하여 로그인 완료 밎 토큰 발급
                response.sendRedirect("/auth/loginSuccess");
            }

        }
    }

    @GetMapping("/add-info")
    @Operation(summary = "필수 정보 입력 컨트롤러", description = "신규유저와 기존유저를 구분하고 알맞은 값을 리턴")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "nickname: 사용자가 사용할 닉네임, address: 사용자가 사용할 주소, new: true: 신규 사용자, false: 기존 사용자"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션에 등록된 id와 일치하는 유저 정보가 없음 > 다시 로그인 시도 필요")
    })
    public ApiResponse<CompulsoryFieldResponseDto> getCompulsoryInfo(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        CompulsoryFieldResponseDto responseDto;

        // 세션으로 유저id를 받았을때 (기존 유저의 정보가 손상된 경우)
        if (userId != null) {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                responseDto = new CompulsoryFieldResponseDto(user.getNickName(), user.getAddress(), false);
            } else {
                throw new CustomException(ErrorCode.USERNAME_NOT_FOUND); // 받은 id로 유저식별이 불가능할때
            }

        } else {
            sdkService.validateSessionData(session);
            responseDto = new CompulsoryFieldResponseDto(null, null, true);
        }
        return new ApiResponse<>(HttpStatus.OK, responseDto);
    }



    @PostMapping("/add-info")
    @Operation(summary = "입력받은 추가정보와 기존의 정보를 토대로 유저객체 생성", description = "신규 유저")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "로그인 완료창 리다이렉션 실패")
    })
    public void getExistingInfo (
            @RequestBody NicknameAddressDto nicknameAddressDto,
            HttpSession session,
            HttpServletResponse response
    ) {
        // 세션에서 유저정보 추출
        SessionDataDto sessionDataDto = sdkService.getSessionData(session);

        User user = User.builder()
                .userName(sessionDataDto.getUsername())
                .nickName(nicknameAddressDto.getNickName())
                .tier(Tier.BRONZE)
                .password(null)
                .email(sessionDataDto.getEmail())
                .totalScore(0)
                .isDone(true)
                .role(Role.ROLE_USER)
                .address(nicknameAddressDto.getAddress())
                .provider(sessionDataDto.getProvider())
                .picture("https://avatar.iran.liara.run/username?username=" + sessionDataDto.getEmail())
                .isDeleted(false)
                .build();
        userRepository.save(user);

        // 세션에서 불필요한 정보 제거
        session.removeAttribute("email");
        session.removeAttribute("username");
        session.removeAttribute("provider");

        // 세션에 유저id 세팅
        session.setAttribute("userId", user.getId());

        // 로그인 완료로 리다이렉트
        try{
            response.sendRedirect("/auth/loginSuccess");
        } catch (IOException e){
            throw new CustomException(ErrorCode.REDIRECT_FAILED);
        }

    }

    @PutMapping("/add-info")
    @Operation(summary = "소실된 필수 정보 업데이트", description = "기존 유저")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "로그인 완료창 리다이렉션 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "세션에 등록된 id와 일치하는 유저 정보가 없음 > 다시 로그인 시도 필요")
    })
    public void addInfo(
            @RequestBody NicknameAddressDto nicknameAddressDto,
            HttpSession session,
            HttpServletResponse response
    ) {
        // 세션에서 id 추출
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.INVALID_SESSION);
        }


        // 수정된 정보로 유저 업데이트
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.addInfo(nicknameAddressDto.getNickName(), nicknameAddressDto.getAddress());
            userRepository.save(user);
            // 로그인 완료로 리다이렉트
            try{
                response.sendRedirect("/auth/loginSuccess");
            } catch (IOException e){
                throw new CustomException(ErrorCode.REDIRECT_FAILED);
            }
        } else {
            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        }
    }


    @GetMapping("/loginSuccess")
    @Operation(summary = "소셜 로그인 성공" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "accessToken: 엑세스 토큰 , refreshToken: 리프레쉬 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "유저id 암호화 실패 > 다시 로그인 시도 필요")

    })
    public ApiResponse<LoginResponseDto> loginSuccess(
            HttpSession session,
            HttpServletResponse response
    ) {

        Long userId = (Long) session.getAttribute("userId");

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
