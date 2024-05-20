package com.jpms.codinggame.controller;

import com.jpms.codinggame.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    //Game Entrance
//    @GetMapping("/game")
//    public ApiResponse<QuestionResDto> startGame(){
//        return new ApiResponse<>(HttpStatus.OK,new QuestionResDto());
//    }

    //정답 확인하기
//    @GetMapping("/game/{}/answer")

}
