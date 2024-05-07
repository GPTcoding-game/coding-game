package com.jpms.codinggame.global.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//ResponseEntity 구현체
public class ApiResponse<T> extends ResponseEntity<T> {
    //메서드 재정의
    public ApiResponse(HttpStatus httpStatus, T data){
        super(data,httpStatus);
    }

}