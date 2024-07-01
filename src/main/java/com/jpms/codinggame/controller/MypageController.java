package com.jpms.codinggame.controller;


import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.QnaResDto;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.service.QnaService;
import com.jpms.codinggame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name ="마이페이지 Controller", description = "")
@RestController
@Slf4j
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

    private final UserService userService;
    private final QnaService qnaService;

    @GetMapping("/update")
    @Operation(summary = "현재 유저 정보 불러오기" , description = "비밀번호는 고의적으로 리턴하지않음")
    public ApiResponse<NicknameAddressDto> getChangeableInfo(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        return new ApiResponse<>(HttpStatus.OK, new NicknameAddressDto(user.getNickName(), user.getAddress()));
    }

    @PutMapping("/update")
    @Operation(summary = "유저 정보 수정" , description = "")
    public ApiResponse<ResponseDto> updateUserInfo(
           @RequestBody UpdateUserInfoDto dto,
            Authentication authentication
    ){
        userService.updateUser(dto, authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("유저 정보 수정 완료"));
    }


    @GetMapping("/info")
    @Operation(summary = "내 정보 요청", description = "")
    public ApiResponse<UserInfoDto> getUserInfo(Authentication authentication){
        UserInfoDto userInfo = userService.getUserInfo(authentication);
        return new ApiResponse<>(HttpStatus.OK, userInfo);
    }

    @GetMapping("/myqna")
    @Operation(summary = "내 질문 리스트 요청", description = "")
    public ApiResponse<List<QnaResDto>> myQuestion (Authentication authentication){
        return new ApiResponse<>(HttpStatus.OK,qnaService.getMyQna(authentication));
    }

    //  티어 나오는지 to String없이
    // 랭크 구현 >> 랭크서비스 가져와서 my랭크  userInfoDTO에 넣어면된다

    // 둘 다 완
}
