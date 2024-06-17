package com.jpms.codinggame.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND,"유저 정보가 존재하지 않습니다."),
    POSSIBLE_COUNT_EXCEPTION(HttpStatus.BAD_REQUEST,"게임 참여횟수가 초과되었습니다"),
    OUT_OF_QUESTION_INDEX(HttpStatus.BAD_REQUEST,"더 이상 불러올 문제가 없습니다."),
    INCORRECT_QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND,"틀린 문제가 없습니다."),
    INVALID_QUESTION_ID(HttpStatus.BAD_REQUEST,"잘못 된 접근입니다. (question ID is invalid)"),
    INVALID_QNA_ID(HttpStatus.BAD_REQUEST,"잘못 된 접근입니다. (QnA ID is invalid"),
    DELETE_GRANT_EXCEPTION(HttpStatus.BAD_REQUEST,"삭제할 권한이 없습니다.");


    private HttpStatus httpStatus;
    private String message;


}
