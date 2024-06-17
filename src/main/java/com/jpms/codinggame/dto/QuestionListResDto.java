package com.jpms.codinggame.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionListResDto {
    private List<QuestionResDto> questionResDtoList;
    private Long nextCursor;
}
