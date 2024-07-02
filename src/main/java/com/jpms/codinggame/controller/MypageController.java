package com.jpms.codinggame.controller;


import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.QnaResDto;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.service.QnaService;
import com.jpms.codinggame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "nickName: 사용자의 현재 닉네임 , address: 사용자의 현재 주소")
    })
    public ApiResponse<NicknameAddressDto> getChangeableInfo(Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();
        return new ApiResponse<>(HttpStatus.OK, new NicknameAddressDto(user.getNickName(), user.getAddress()));
    }

    @PutMapping("/update")
    @Operation(summary = "유저 정보 수정" , description = "비밀번호 일치 확인 로직은 프론트에서 처리")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "유저 정보 수정이 완료되었다는 메시지 > 다시 마이페이지로 이동")
    })
    public ApiResponse<ResponseDto> updateUserInfo(
           @RequestBody UpdateUserInfoDto dto,
            Authentication authentication
    ){
        userService.updateUser(dto, authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("유저 정보 수정 완료"));
    }


    @GetMapping("/info")
    @Operation(summary = "내 정보 요청", description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "userName: 사용자 계정, " +
                            "nickName: 사용자의 현재 닉네임 , " +
                            "totalScore: 사용자의 현재 총 점수 ," +
                            "tier: 사용자의 현재 티어 ," +
                            "address: 사용자의 주소" +
                            "todayRank: 사용자의 오늘 랭킹")
    })
    public ApiResponse<UserInfoDto> getUserInfo(Authentication authentication){
        UserInfoDto userInfo = userService.getUserInfo(authentication);
        return new ApiResponse<>(HttpStatus.OK, userInfo);
    }

    @GetMapping("/myqna")
    @Operation(summary = "내 질문 리스트 요청", description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
                    description = "qnaId: 질문 번호, " +
                            "content: 질문의 내용 , " +
                            "title: 질문의 제목 ," +
                            "time: 질문의 생성 시간 ," +
                            "nickname: 사용자의 닉네임" +
                            "commentVolume: 해당 질문의 댓글 수")
    })
    public ApiResponse<List<QnaResDto>> myQuestion (Authentication authentication){
        return new ApiResponse<>(HttpStatus.OK,qnaService.getMyQna(authentication));
    }

}
