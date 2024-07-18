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
    private Long qnaId;
    private String content;
    private String title;
    private LocalDate time;
    private String nickname;
    private String pictureUrl;
    private int commentVolume;
//    private String userName;

    public static QnaResDto fromEntity(Qna qna){
        return QnaResDto
                .builder()
                .qnaId(qna.getId())
                .title(qna.getTitle())
                .content(qna.getContent())
                .time(qna.getTime())
                .nickname(qna.getUser().getNickName())
                .commentVolume(qna.getCommentList().size())
                .pictureUrl(qna.getUser().getPicture())
                .build();
    }
}
