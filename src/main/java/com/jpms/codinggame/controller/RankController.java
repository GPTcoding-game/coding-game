package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.RankResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.service.RankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name= "랭킹 Controller", description = "당일 랭킹, 누적 랭킹 호출 API")
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
    @Operation(summary = "오늘의 랭킹 불러오기(List)" , description = "내림차순으로 전체 List 뽑아옴. 일부 뽑아쓰기")
    public ApiResponse<List<RankResDto>> getTodayRank(){
        return new ApiResponse<>(HttpStatus.OK,rankService.getTodayRank());
    }

    /*
    * 누적 랭킹
    * */
    @GetMapping("/rank/all")
    @Operation(summary = "누적 점수 랭킹(List)" , description = "내림차순으로 상위 50명 List 뽑아옴. 일부 뽑아쓰기")
    public ApiResponse<List<RankResDto>> getAllDayRank(){
        return new ApiResponse<>(HttpStatus.OK,rankService.getAllDayRank());
    }

    /*
    * 지역별 랭킹 (전부 완성 후 개발)
    * */
}
