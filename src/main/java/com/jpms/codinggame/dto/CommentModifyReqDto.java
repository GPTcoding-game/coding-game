package com.jpms.codinggame.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentModifyReqDto {
    private String content;
    private LocalDateTime time;
}
