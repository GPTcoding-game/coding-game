package com.jpms.codinggame.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponseDto {
    private String errorMessage;

    public static ErrorResponseDto getInstance(String errorMessage){
        return new ErrorResponseDto(errorMessage);
    }
    //singleton
}
