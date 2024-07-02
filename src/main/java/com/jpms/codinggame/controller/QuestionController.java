package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.QuestionListResDto;
import com.jpms.codinggame.dto.QuestionResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name="문제 호출 Controller",description = "틀린 문제 , 유형별 문제 , 역대 출제된 문제 가져오기")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class QuestionController {
    private final QuestionService questionService;

    /*
    * 검색 기능 ( cursor )
    * */
    @GetMapping("/question/past")
    @Operation(
            summary = "지난 문제 요청",
            description = "쿼리파라미터 questionType(String),date(String),incorrect(boolean, 디폴트 false) ")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지난 문제 요청이 정상적으로 처리되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "문제가 전부 호출 되었거나, 가져올 문제가 없음(틀린 문제가 없음)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "유저 정보를 찾을 수 없음")
    })
    public ApiResponse<QuestionListResDto> getPastQuestionListByCondition(
            @RequestParam(defaultValue = "0") Long cursor,
            @RequestParam(name="questionType", required = false) String questionType,
            @RequestParam(name="date", required = false) String date,
            @RequestParam(name="incorrect", defaultValue = "false", required = false) boolean incorrect,
            Authentication authentication
    ){
        log.info(questionType);
        log.info(date);
        log.info(String.valueOf(incorrect));
        QuestionListResDto listResDto = new QuestionListResDto();
        listResDto.setQuestionResDtoList(questionService.getQuestionListByCondition(authentication,date,questionType,incorrect,cursor));
        listResDto.setNextCursor(cursor + 5);
        return new ApiResponse<>(HttpStatus.OK,listResDto);
    }


//    /*
//    * 틀린 Question 리스트 뽑기
//    * */
//    @GetMapping("/question/incorrect")
//    @Operation(summary = "틀린 문제 리스트 요청" , description = "")
//    public ApiResponse<List<QuestionResDto>> getIncorrectQuestionList(
//            Authentication authentication
//    ){
//        return new ApiResponse<>(HttpStatus.OK,questionService.getIncorrectQuestionList(authentication));
//    }
//
//    /*
//    * Question(Python, Java, C, SQL, CS 지식) 별 문제 리스트 뽑기(당일 출제 된 문제는 제외)
//    * */
//    @GetMapping("/question/type")
//    @Operation(summary = "유형별 문제 리스트 요청" , description = "유형에는 JAVA, SQL, C, Python 등 이 있음")
//    public ApiResponse<List<QuestionResDto>> getQuestionListByType(
//            @RequestParam String questionType
//    ){
//        return new ApiResponse<>(HttpStatus.OK,questionService.getQuestionListByType(questionType));
//    }

    // /*
    // * 역대 출제 된 문제 리스트 뽑기
    // * (로직 수정 >> 지난 문제 전체 가져오기 >> 날짜별 문제 가져오기 (당일 문제 제외)
    // * */
    // @GetMapping("/question/past")
    // @Operation(summary = "날짜별 문제 가져오기로 바꿈" , description = "")
    // public ApiResponse<List<QuestionResDto>> getPastQuestionList(){
    //     return new ApiResponse<>(HttpStatus.OK,questionService.getPastQuestionAll());
    // }
}
