package com.jpms.codinggame.controller;

import com.jpms.codinggame.config.GPTConfig;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.service.GPTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name ="GPT 문제생성 Controller", description = "GPT 문제생성 CRUD API")
@RequestMapping("/bot")
@Slf4j
public class GPTController {

    private final GPTService gptService;



    @GetMapping("/chat")
    @Operation(summary = "문제 생성 요청" , description = "")
    public ApiResponse<ResponseDto> generate(){
        gptService.createQuestion(GPTConfig.model, GPTConfig.apiURL, GPTConfig.template);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("문제 생성 완료"));
    }







}