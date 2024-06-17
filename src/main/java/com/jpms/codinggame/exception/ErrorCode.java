package com.jpms.codinggame.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND,"유저 정보가 존재하지 않습니다."),
    VALIDATION_EXCEPTION(HttpStatus.NOT_FOUND,"인증에 실패하였습니다."),
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND,"유저 정보가 존재하지 않습니다."),
    POSSIBLE_COUNT_EXCEPTION(HttpStatus.BAD_REQUEST,"게임 참여횟수가 초과되었습니다"),
    EXISTING_USERNAME_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 사용중인 아이디입니다"),
    EXISTING_EMAIL_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 사용중인 이메일입니다"),
    EMAIL_VERIFICATION_FAILED(HttpStatus.BAD_REQUEST,"이메일 인증에 실패하였습니다"),
    PASSWORD_CHECK_FAILED(HttpStatus.BAD_REQUEST,"두 비밀번호가 일치하지 않습니다"),
    PASSWORD_INVALID(HttpStatus.BAD_REQUEST,"비밀번호가 일치하지 않습니다"),
    EMAIL_MISMATCH_EXCEPTION(HttpStatus.NOT_FOUND,"이메일이 일치하지 않습니다."),
    EXISTING_NICKNAME_EXCEPTION(HttpStatus.BAD_REQUEST,"이미 사용중인 닉네임입니다"),
    ENCRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"암호화에 실패하였습니다."),
    DECRYPTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,"복호화에 실패하였습니다.");



    private HttpStatus httpStatus;
    private String message;


}
