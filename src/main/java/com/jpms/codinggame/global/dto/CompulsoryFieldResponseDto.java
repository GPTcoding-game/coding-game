package com.jpms.codinggame.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompulsoryFieldResponseDto {
    private String nickName;

    private String address;

    private boolean isNew;
}
