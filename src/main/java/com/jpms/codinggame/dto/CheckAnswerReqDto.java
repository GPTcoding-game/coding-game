package com.jpms.codinggame.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckAnswerReqDto {
    //틀린 문제 questionNo
    private List<Integer> incorrectNumber;
    private int score;

}
