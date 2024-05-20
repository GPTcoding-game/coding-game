package com.jpms.codinggame.controller;

import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.global.dto.GPTRequestDto;
import com.jpms.codinggame.global.dto.GPTResponseDto;
import com.jpms.codinggame.service.GPTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
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
    public String chat(){
        Question question = gptService.createQuestion(model, apiURL, template);
        return "문제: " + question.getContent() + "\n답: " + question.getAnswer();
    }

}