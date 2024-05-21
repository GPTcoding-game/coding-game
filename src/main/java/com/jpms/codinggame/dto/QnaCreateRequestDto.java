package com.jpms.codinggame.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class QnaCreateRequestDto {
    private String content;
    private String title;
    private LocalDate time;
}
