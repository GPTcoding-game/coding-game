package com.jpms.codinggame.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND,"유저 정보가 존재하지 않습니다."),
    POSSIBLE_COUNT_EXCEPTION(HttpStatus.BAD_REQUEST,"게임 참여횟수가 초과되었습니다");


    private HttpStatus httpStatus;
    private String message;


}
