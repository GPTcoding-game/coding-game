package com.jpms.codinggame.global.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class GPTRequestDto {
    private String model;
    private List<QuestionDto> questionDtoList;

    public GPTRequestDto(String model, String prompt) {
        this.model = model;
        this.questionDtoList =  new ArrayList<>();
        this.questionDtoList.add(new QuestionDto("user", prompt));
    }
}
