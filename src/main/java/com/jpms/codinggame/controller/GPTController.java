package com.jpms.codinggame.controller;

import com.jpms.codinggame.config.GPTConfig;
import com.jpms.codinggame.global.dto.*;
import com.jpms.codinggame.service.GPTService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@RestController
@RequiredArgsConstructor
@Tag(name ="GPT 문제생성 Controller", description = "GPT 문제생성 CRUD API")
@RequestMapping("/bot")
@Slf4j
public class GPTController {

    private final GPTService gptService;


    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @GetMapping("/chat")
    @Operation(summary = "문제 생성 요청" , description = "")
    public ApiResponse<ResponseDto> generate(){
        gptService.createQuestion(model, apiURL, template);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("문제 생성 완료"));
    }







}