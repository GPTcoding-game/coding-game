package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.CheckAnswerReqDto;
import com.jpms.codinggame.dto.CheckAnswerReqDto2;
import com.jpms.codinggame.dto.QuestionResDto;
import com.jpms.codinggame.exception.CustomException;
import com.jpms.codinggame.exception.ErrorCode;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ResponseDto;
import com.jpms.codinggame.service.GameService;
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

@Tag(name ="게임 Controller", description = "게임 시작, 채점, 중도 포기 로직")
@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final QuestionService questionService;

    /*
    게임시작 (참여가능여부 확인)
    풀 문제의 갯수 정하기 ( query parameter 로 갯수 받아오기 )  /game/start?volume=10
    */
    @GetMapping("/game/start")
    @Operation(summary = "게임 시작 요청" , description = "isDone 필드 false 이면 exception 날림")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게임 정상적으로 시작, 문제 리스트 리턴"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "게임 횟수가 초과되어 요청 거부"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<List<QuestionResDto>> startGame(
            Authentication authentication
    ){
        //게임 참여 가능 여부 파악
        if(!gameService.isDone(authentication)) throw new CustomException(ErrorCode.POSSIBLE_COUNT_EXCEPTION);

        //참여횟수 1회 차감
        gameService.deductPossibleCount(authentication);

        return new ApiResponse<>(HttpStatus.OK,questionService.getQuestionList(authentication));
    }

    /*
     * 오늘 문제 채점 로직
     * */
    @GetMapping("/game/answer")
    @Operation(summary = "오늘 문제 채점 요청" , description = "questionId(long) , isCorrect(boolean)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채점 요청이 정상적으로 처리되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID 가 정상적이지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> checkAnswerToday(
            Authentication authentication,
            @RequestBody CheckAnswerReqDto2 dto
    ){
        gameService.checkAnswerTodayQuestion(authentication,dto);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("채점 저장 완료"));
    }

    /*
     * 지난 문제 채점 로직
     * */
    @GetMapping("/game/answer/past")
    @Operation(summary = "지난 문제 채점 요청" , description = "틀린문제 QuestionNo 를 리스트 형태로 만들어 요청해야함.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "채점 요청이 정상적으로 처리되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID 가 정상적이지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> checkAnswerPast(
            Authentication authentication,
            @RequestBody CheckAnswerReqDto2 dto
    ){
        gameService.checkAnswerPastQuestion(authentication,dto);
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("채점 저장 완료"));
    }

    /*
    * 게임 중도 포기 로직
    * */
//    @GetMapping("/game/stop")
//    @Operation(summary = "게임 중도 포기 로직" , description = "")
//    public ApiResponse<ResponseDto> stopGame(
//            Authentication authentication
//    ){
//        gameService.stopGame(authentication);
//        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance(""));
//    }


}
