package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.QuestionResDto;
import com.jpms.codinggame.entity.Question;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class QuestionController {
    private final QuestionService questionService;
    private final RedisTemplate<String, String> redisTemplate;

    /*
    * 틀린 Question 리스트 뽑기
    * */
//    @GetMapping("/question/incorrect")
//    public ApiResponse<List<QuestionResDto>> getIncorrectQuestionList(){}

    /*
    * Question(Python, Java, C, SQL, CS 지식) 별 문제 리스트 뽑기(당일 출제 된 문제는 제외)
    * */
    @GetMapping("/question/type")
    public ApiResponse<List<QuestionResDto>> getQuestionListByType(
            @RequestParam String questionType
    ){
        return new ApiResponse<>(HttpStatus.OK,questionService.getQuestionListByType(questionType));
    }

    /*
    * 역대 출제 된 문제 리스트 뽑기
    * */
//    @GetMapping("/question/past")
//    public ApiResponse<List<QuestionResDto>> getPastQuestionList(){}

}
