package com.jpms.codinggame.exception;

import com.jpms.codinggame.global.dto.ApiResponse;
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
    public ApiResponse<ResponseDto> customExceptionHandler(CustomException e) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST,ResponseDto.getInstance(e.getMessage()));
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ResponseDto> handleSignatureException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDto.getInstance("토큰이 유효하지 않습니다."));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ResponseDto> handleMalformedJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDto.getInstance("올바르지 않은 토큰입니다."));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ResponseDto> handleExpiredJwtException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseDto.getInstance("토큰이 만료되었습니다. 다시 로그인해주세요."));
    }

    //오늘 상의
//    @ExceptionHandler(ValidationException.class)
//    public ResponseEntity<ResponseDto> handleValidationException(ValidationException e) {
//        int combinedErrorCode = e.getCombinedErrorCode();
//        String errorMessage = e.getCombinedErrorMessage();
//
//        HttpStatus status;
//        switch (combinedErrorCode) {
//            case 1: // EXISTING_USERNAME_EXCEPTION
//                status = HttpStatus.UNAUTHORIZED;
//                break;
//            case 2: // EXISTING_NICKNAME_EXCEPTION
//                status = HttpStatus.FORBIDDEN;
//                break;
//            case 3: // EXISTING_USERNAME_EXCEPTION & EXISTING_NICKNAME_EXCEPTION
//                status = HttpStatus.FORBIDDEN;
//                break;
//            case 4: // EMAIL_VERIFICATION_FAILED
//                status = HttpStatus.NOT_FOUND;
//                break;
//            case 5: // EXISTING_USERNAME_EXCEPTION & EMAIL_VERIFICATION_FAILED
//                status = HttpStatus.FORBIDDEN;
//                break;
//            case 6: // EXISTING_NICKNAME_EXCEPTION & EMAIL_VERIFICATION_FAILED
//                status = HttpStatus.FORBIDDEN;
//                break;
//            case 7: // EXISTING_USERNAME_EXCEPTION & EXISTING_NICKNAME_EXCEPTION & EMAIL_VERIFICATION_FAILED
//                status = HttpStatus.FORBIDDEN;
//                break;
//            default:
//                status = HttpStatus.BAD_REQUEST;
//                break;
//        }
//
//        return new ResponseEntity<>(ResponseDto.getInstance(errorMessage), status);
//    }
}


