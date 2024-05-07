package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.QnaCreateRequestDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ResponseDto;
import com.jpms.codinggame.service.QnaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QnaController {
    private final QnaService qnaService;
    //질문 생성 요청
    @PostMapping("/question/{}/qna")
    public ApiResponse<ResponseDto> createQna(
            @PathVariable("questionId") Long questionId,
            @RequestBody QnaCreateRequestDto dto
    ){
        qnaService.createQna(questionId,dto);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("질문 생성 완료"));
    }

    //질문 리스트 가져오기 요청
    //질문 삭제 요청
    //질문 수정 요청
}
