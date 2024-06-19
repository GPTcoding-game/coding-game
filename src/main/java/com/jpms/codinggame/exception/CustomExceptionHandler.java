package com.jpms.codinggame.exception;

import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ErrorResponseDto;
import com.jpms.codinggame.global.dto.ResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ApiResponse<ErrorResponseDto> customExceptionHandler(CustomException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST,ErrorResponseDto.getInstance(e.getMessage()));
    }

    @ExceptionHandler(SignatureException.class)
    public ApiResponse<ErrorResponseDto> handleSignatureException() {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED,ErrorResponseDto.getInstance("토큰이 유효하지 않음."));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ApiResponse<ErrorResponseDto> handleMalformedJwtException() {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED,ErrorResponseDto.getInstance("올바르지 않은 토큰."));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ApiResponse<ErrorResponseDto> handleExpiredJwtException() {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED,ErrorResponseDto.getInstance("토큰이 만료되었음. 다시 로그인 바람."));
    }

    //오늘 상의
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDto> handleValidationException(ValidationException e) {
        int combinedErrorCode = e.getCombinedErrorCode();
        String errorMessage = e.getCombinedErrorMessage();

        HttpStatus status;
        switch (combinedErrorCode) {
            case 1: // EXISTING_USERNAME_EXCEPTION
                status = HttpStatus.UNAUTHORIZED;
                break;
            case 2: // EXISTING_NICKNAME_EXCEPTION
                status = HttpStatus.PAYMENT_REQUIRED;
                break;
            case 3: // EXISTING_USERNAME_EXCEPTION & EXISTING_NICKNAME_EXCEPTION
                status = HttpStatus.FORBIDDEN;
                break;
            case 4: // EMAIL_VERIFICATION_FAILED
                status = HttpStatus.NOT_FOUND;
                break;
            case 5: // EXISTING_USERNAME_EXCEPTION & EMAIL_VERIFICATION_FAILED
                status = HttpStatus.METHOD_NOT_ALLOWED;
                break;
            case 6: // EXISTING_NICKNAME_EXCEPTION & EMAIL_VERIFICATION_FAILED
                status = HttpStatus.NOT_ACCEPTABLE;
                break;
            case 7: // EXISTING_USERNAME_EXCEPTION & EXISTING_NICKNAME_EXCEPTION & EMAIL_VERIFICATION_FAILED
                status = HttpStatus.PROXY_AUTHENTICATION_REQUIRED;
                break;
            default:
                status = HttpStatus.BAD_REQUEST;
                break;
        }

        return new ResponseEntity<>(ResponseDto.getInstance(errorMessage), status);
    }
}


