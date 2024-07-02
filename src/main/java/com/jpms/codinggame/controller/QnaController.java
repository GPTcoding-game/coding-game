package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.QnaCreateRequestDto;
import com.jpms.codinggame.dto.QnaModifyRequestDto;
import com.jpms.codinggame.dto.QnaResDto;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ResponseDto;
import com.jpms.codinggame.service.QnaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "질문 Controller", description = "질문 CRUD API")
@RestController
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class QnaController {
    private final QnaService qnaService;
    //질문 생성 요청
    @PostMapping("/question/{questionId}/qna")
    @Operation(summary = "질문 생성 요청" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "질문이 정상적으로 DB에 저장되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QUESTION ID 가 정상적이지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> createQna(
            @PathVariable("questionId") Long questionId,
            @RequestBody QnaCreateRequestDto dto,
            Authentication authentication
    ){
        qnaService.createQna(questionId,dto,authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("질문 생성 완료"));
    }

    //질문 삭제 요청

    @DeleteMapping("/qna/{qnaId}")
    @Operation(summary = "질문 삭제 요청" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "질문 삭제 요청이 정상적으로 처리되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID 가 정상적이지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제할 권한이 없음.(유저정보 불일치)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> deleteQna(
            @PathVariable("qnaId") Long qnaId,
            Authentication authentication
    ){
        qnaService.deleteQna(qnaId,authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("질문 삭제 완료"));
    }
    //질문 수정 요청
    @PutMapping("/question/{questionId}/qna/{qnaId}")
    @Operation(summary = "질문 수정 요청" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "질문수정 요청이 정상적으로 처리되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID 또는 QUESTION ID 가 정상적이지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> modifyQna(
            @PathVariable("questionId") Long questionId,
            @PathVariable("qnaId") Long qnaId,
            @RequestBody QnaModifyRequestDto dto,
            Authentication authentication
    ){
        qnaService.modifyQna(qnaId,questionId,dto,authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("질문 수정 완료"));
    }

    //질문 리스트 가져오기 요청
    @GetMapping("/question/{questionId}/qnas")
    @Operation(summary = "질문 리스트 요청(질문 전체 조회)" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "질문 리스트 호출이 정상적으로 처리되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QUESTION ID 가 정상적이지 않음")
    })
    public ApiResponse<List<QnaResDto>> getQnaList(
            @PathVariable("questionId") Long questionId
    ){
        return new ApiResponse<>(HttpStatus.OK,qnaService.getQnaList(questionId));
    }

    @GetMapping("/question/{questionId}/qna/{qnaId}")
    @Operation(summary = "단일 질문 요청(질문 조회)" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "질문 조회 요청이 정상적으로 처리되었음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID 가 정상적이지 않음")
    })
    public ApiResponse<QnaResDto> getQna(
            @PathVariable("questionId") Long questionId,
            @PathVariable("qnaId") Long qnaId
    ){
        return new ApiResponse<>(HttpStatus.OK,qnaService.getQna(qnaId));
    }

    @GetMapping("/question/recent")
    @Operation(summary = "최근 생성된 Qna 5개 가져오기", description = "질문게시판 탭 화면에 보여질 최근생성 QNA")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "질문 리스트 호출이 정상적으로 처리되었음")
    })
    public ApiResponse<List<QnaResDto>> getRecentQna(){
        return new ApiResponse<>(HttpStatus.OK,qnaService.getRecent5Qna());
    }
}
