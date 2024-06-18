package com.jpms.codinggame.exception;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationException extends RuntimeException {
    private final List<ValidationErrorCode> errorCodes;


    public ValidationException(List<ValidationErrorCode> errorCodes) {
        this.errorCodes = errorCodes;
    }

    public List<ValidationErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public int getCombinedErrorCode() {
        return errorCodes.stream().mapToInt(ValidationErrorCode::getCode).sum();
    }

    public String getCombinedErrorMessage() {
        return errorCodes.stream().map(ValidationErrorCode::getMessage).collect(Collectors.joining(", "));
    }
}
