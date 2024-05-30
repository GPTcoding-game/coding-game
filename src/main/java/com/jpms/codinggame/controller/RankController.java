package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.RankResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;

    /*
    * 당일 랭킹
    * */
    @GetMapping("/rank/today")
    public ApiResponse<List<RankResDto>> getTodayRank(){
        return new ApiResponse<>(HttpStatus.OK,rankService.getTodayRank());
    }

    /*
    * 누적 랭킹
    * */
    @GetMapping("/rank/all")
    public ApiResponse<List<RankResDto>> getAllDayRank(){
        return new ApiResponse<>(HttpStatus.OK,rankService.getAllDayRank());
    }

    /*
    * 지역별 랭킹 (전부 완성 후 개발)
    * */
}
