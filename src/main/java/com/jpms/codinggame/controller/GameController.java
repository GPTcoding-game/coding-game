package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.CheckAnswerReqDto;
import com.jpms.codinggame.dto.CheckAnswerReqDto2;
import com.jpms.codinggame.dto.QuestionResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ResponseDto;
import com.jpms.codinggame.service.GameService;
import com.jpms.codinggame.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ApiResponse<List<QuestionResDto>> startGame(
            Authentication authentication
    ){
        /*
        * 이 부분은 main 화면에 처음 들어올때 생성하는거 어떤지
        * */
        //REDIS DATA 저장되어있는지 확인
        if(gameService.isFirst(authentication)){
            log.info("isEmpty");
            //SET REDIS DATA
            gameService.setRedisData(authentication);
        }

        //참여횟수 1회 차감
        gameService.deductPossibleCount(authentication);

        //게임 참여 가능 여부 파악
        if(!gameService.isDone(authentication)) throw new RuntimeException("게임 참여횟수 초과");


        return new ApiResponse<>(HttpStatus.OK,questionService.getQuestionList());
    }

    /*
     * 오늘 문제 채점 로직
     * */
    @GetMapping("/game/answer")
    @Operation(summary = "오늘 문제 채점 요청" , description = "questionId(long) , isCorrect(boolean)")
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
