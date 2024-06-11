package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.MainInfoResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MainController {

    private final UserService userService;

    @GetMapping("/main/info")
    @Operation(summary = "main 화면 userinfo 요청" , description = "최초 입장 시 redis 에 데이터 새로 생성 됨")
    public ApiResponse<MainInfoResDto> mainEntrance(Authentication authentication){
        //처음 입장인지 확인 ( Redis data 생성 )
        userService.firstEntrance(authentication);

        return new ApiResponse<>(HttpStatus.OK,userService.getMainInfo(authentication));
    }

}
