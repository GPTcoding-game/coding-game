package com.jpms.codinggame.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CommentModifyRequestDto {
    private String content;
    private LocalDate time;
}
