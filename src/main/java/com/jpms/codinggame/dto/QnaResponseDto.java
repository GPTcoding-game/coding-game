package com.jpms.codinggame.dto;

import com.jpms.codinggame.entity.Qna;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class QnaResponseDto {
    private String content;
    private String title;
    private LocalDateTime time;
//    private String userName;

    public static QnaResponseDto fromEntity(Qna qna){
        return QnaResponseDto
                .builder()
                .title(qna.getTitle())
                .content(qna.getContent())
                .time(qna.getTime())
                .build();
    }
}
