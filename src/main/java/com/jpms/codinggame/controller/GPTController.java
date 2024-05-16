package com.jpms.codinggame.controller;

import com.jpms.codinggame.global.dto.GPTRequestDto;
import com.jpms.codinggame.global.dto.GPTResponseDto;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/bot")
public class GPTController {
    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate template;

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "prompt")String prompt){
        GPTRequestDto request = new GPTRequestDto(model, prompt);
        GPTResponseDto chatGPTResponse =  template.postForObject(apiURL, request, GPTResponseDto.class);
        return chatGPTResponse.getChoices().get(0).getMessage().getContent();
    }

}