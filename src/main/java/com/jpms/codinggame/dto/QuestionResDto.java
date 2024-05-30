package com.jpms.codinggame.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionResDto {
    private Long questionId;
    private int questionNo;
    private String content;
    private String choice;
    private String answer;
}
