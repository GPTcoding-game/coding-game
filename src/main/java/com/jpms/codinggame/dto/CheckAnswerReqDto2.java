package com.jpms.codinggame.dto;

import lombok.Data;
import lombok.Getter;

@Data
public class CheckAnswerReqDto2 {
    private Long questionId;
//    private boolean isCorrect; //필드명을 is~~ 로 하면 역직렬화가 안되어서 제대로 된 값을 못가져옴.
    @Getter
    private boolean correct;

}
