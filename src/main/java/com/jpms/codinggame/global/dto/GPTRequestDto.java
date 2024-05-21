package com.jpms.codinggame.global.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class GPTRequestDto {
    private String model;
    private List<MessageDto> messages;

    private int max_tokens;

    public GPTRequestDto(String model, String prompt, int max_tokens) {
        this.model = model;
        this.messages =  new ArrayList<>();
        this.messages.add(new MessageDto("user", prompt));
        this.max_tokens = 2000;
    }
}
