package com.jpms.codinggame.dto;

import com.jpms.codinggame.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private String nickname;
    private LocalDate time;

    static CommentResponseDto fromEntity(Comment comment){
        return CommentResponseDto
                .builder()
                .id(comment.getId())
                .content(comment.getContent())
                .time(comment.getTime())
                .build();
    }
}
