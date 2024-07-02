package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.ComplaintReqDto;
import com.jpms.codinggame.dto.ComplaintResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ResponseDto;
import com.jpms.codinggame.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ComplaintController {
     private final ComplaintService complaintService;


    @PostMapping("/qna/{qnaId}/complaint")
    @Operation(summary = "QnA 신고 요청")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "qna 신고 작성 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> complaintQna(
            @PathVariable(name="qnaId") Long qnaId,
            @RequestBody ComplaintReqDto dto,
            Authentication authentication
    ){
        complaintService.createComplaint(dto,authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("신고 완료"));
    }

    @PostMapping("/comment/{commentId}/complaint")
    @Operation(summary = "Comment 신고 요청")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 신고 작성 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> complaintComment(
            @PathVariable(name="commentId") Long commentId,
            @RequestBody ComplaintReqDto dto,
            Authentication authentication
    ){
        complaintService.createComplaint(dto,authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("신고 완료"));
    }

    @GetMapping("/complaints")
    @Operation(summary = "신고 목록 리스트 요청", description = "ROLE_ADMIN 만 접근가능하게 수정하기")
    public ApiResponse<List<ComplaintResDto>> getComplaintList(){
        return new ApiResponse<>(HttpStatus.OK,complaintService.getComplaintAll());
    }


}
