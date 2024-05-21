package com.jpms.codinggame.dto;

import com.jpms.codinggame.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentResDto {
    private String content;
    private LocalDateTime time;

    static CommentResDto fromEntity(Comment comment){
        return CommentResDto
                .builder()
                .content(comment.getContent())
                .time(comment.getTime())
                .build();
    }
}
