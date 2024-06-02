package com.jpms.codinggame.dto;

import com.jpms.codinggame.entity.Qna;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class QnaResDto {
    private String content;
    private String title;
    private LocalDate time;
    private String nickname;
//    private String userName;

    public static QnaResDto fromEntity(Qna qna){
        return QnaResDto
                .builder()
                .title(qna.getTitle())
                .content(qna.getContent())
                .time(qna.getTime())
                .nickname(qna.getUser().getNickName())
                .build();
    }
}
