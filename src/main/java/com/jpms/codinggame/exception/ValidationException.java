package com.jpms.codinggame.exception;

import java.util.List;

public class ValidationException extends CustomException {
    private final List<ErrorCode> errorCodes;

    public ValidationException(List<ErrorCode> errorCodes) {
        super(ErrorCode.VALIDATION_EXCEPTION);
        this.errorCodes = errorCodes;
    }

    public List<ErrorCode> getErrorCodes() {
        return errorCodes;
    }
}
