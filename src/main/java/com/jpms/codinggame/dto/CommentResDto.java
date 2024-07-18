package com.jpms.codinggame.dto;

import com.jpms.codinggame.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class CommentResDto {
    private Long commentId;
    private String content;
    private String nickname;
    private String pictureUrl;
    private LocalDate time;

    static CommentResDto fromEntity(Comment comment){
        return CommentResDto
                .builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .time(comment.getTime())
                .build();
    }
}
