package com.jpms.codinggame.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class QnaModifyRequestDto {
    private String title;
    private String content;
}
