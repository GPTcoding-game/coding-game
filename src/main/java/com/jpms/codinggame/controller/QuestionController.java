package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.QuestionResDto;
import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name="문제 호출 Controller",description = "틀린 문제 , 유형별 문제 , 역대 출제된 문제 가져오기")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class QuestionController {
    private final QuestionService questionService;

    /*
    * 틀린 Question 리스트 뽑기
    * */
    @GetMapping("/question/incorrect")
    @Operation(summary = "틀린 문제 리스트 요청" , description = "")
    public ApiResponse<List<QuestionResDto>> getIncorrectQuestionList(
            Authentication authentication
    ){
        return new ApiResponse<>(HttpStatus.OK,questionService.getIncorrectQuestionList(authentication));
    }

    /*
    * Question(Python, Java, C, SQL, CS 지식) 별 문제 리스트 뽑기(당일 출제 된 문제는 제외)
    * */
    @GetMapping("/question/type")
    @Operation(summary = "유형별 문제 리스트 요청" , description = "유형에는 JAVA, SQL, C, Python 등 이 있음")
    public ApiResponse<List<QuestionResDto>> getQuestionListByType(
            @RequestParam String questionType
    ){
        return new ApiResponse<>(HttpStatus.OK,questionService.getQuestionListByType(questionType));
    }

    /*
    * 역대 출제 된 문제 리스트 뽑기
    * (로직 수정 >> 지난 문제 전체 가져오기 >> 날짜별 문제 가져오기 (당일 문제 제외)
    * */
    @GetMapping("/question/past")
    @Operation(summary = "날짜별 문제 가져오기로 바꿈" , description = "")
    public ApiResponse<List<QuestionResDto>> getPastQuestionList(){
        return new ApiResponse<>(HttpStatus.OK,questionService.getPastQuestionAll());
    }

}
