package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.CheckAnswerReqDto;
import com.jpms.codinggame.dto.QuestionResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ResponseDto;
import com.jpms.codinggame.service.GameService;
import com.jpms.codinggame.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ApiResponse<List<QuestionResDto>> startGame(
            Authentication authentication,
            @RequestParam(name = "volume") int volume
    ){
        //게임 참여 가능 여부 파악
        if(gameService.isDone(authentication)) throw new RuntimeException("게임은 하루에 한 번 참여 가능");
        return new ApiResponse<>(HttpStatus.OK,questionService.getQuestionList(volume));
    }

    /*
    * 채점 로직
    * */
    @GetMapping("/game/answer")
    public ApiResponse<ResponseDto> checkAnswer(
            Authentication authentication,
            @RequestBody CheckAnswerReqDto dto
            ){
        //틀린 문제 저장 ( RDB ) QuestionNo
        //당일 점수 저장 ( 당일 랭킹 )
        gameService.checkAnswer(authentication,dto);
        //맞은 개수 저장 ( redis => RDB ) 스케줄러
        //누적 점수 저장 ( 누적 랭킹 ) 스케줄러
        return new ApiResponse<>(HttpStatus.OK, ResponseDto.getInstance("게임 종료"));
    }

    /*
    * 게임 중도 포기 로직
    * */
    @GetMapping("/game/stop")
    public ApiResponse<ResponseDto> stopGame(
//            Authentication authentication
    ){
//        gameService.stopGame(authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("게임 종료"));
    }

}
