package com.jpms.codinggame.exception;

public enum ValidationErrorCode {
    EXISTING_USERNAME_EXCEPTION(1, "이미 사용중인 아이디입니다"),
    EXISTING_NICKNAME_EXCEPTION(2, "이미 사용중인 닉네임입니다"),
    EMAIL_VERIFICATION_FAILED(4, "이메일 인증에 실패하였습니다. 인증 번호를 다시 발급받으세요");

    private final int code;
    private final String message;

    ValidationErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
