package com.jpms.codinggame.controller;

import com.jpms.codinggame.dto.CommentCreateRequestDto;
import com.jpms.codinggame.dto.CommentModifyRequestDto;
import com.jpms.codinggame.dto.CommentResDto;
import com.jpms.codinggame.global.dto.ApiResponse;
import com.jpms.codinggame.global.dto.ResponseDto;
import com.jpms.codinggame.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name ="댓글 Controller", description = "댓글 CRUD API")
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {
    private final CommentService commentService;

    //답글 생성 요청 (답글을 달려면 어디 Question 의 어디 Qna 에 답글을 달지 알아야하는데, 그러면 ?
    @PostMapping("/qna/{qnaId}/comment")
    @Operation(summary = "댓글 생성 요청" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 DB 저장 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID가 유효하지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")

    })
    public ApiResponse<ResponseDto> createComment(
            @PathVariable("qnaId") Long qnaId,
            @RequestBody CommentCreateRequestDto dto,
            Authentication authentication
            ){
        commentService.createComment(qnaId,dto,authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("댓글 생성 완료"));
    }

    //답글(리스트) 가져오기 요청
    @GetMapping("/qna/{qnaId}/comments")
    @Operation(summary = "댓글 리스트 가져오기" , description = "List 형태로 리턴함. (페이징 해야할지 고민)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 리스트 호출"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<List<CommentResDto>> getCommentList(
            @PathVariable("qnaId") Long qnaId
    ){
        return new ApiResponse<>(HttpStatus.OK,commentService.getCommentList(qnaId));
    }

    //답글 삭제 요청
    @DeleteMapping("/comment/{commentId}")
    @Operation(summary = "댓글 삭제 요청" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 삭제 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID가 유효하지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "댓글을 삭제할 권한이 없음 (userId 불일치)"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> deleteComment(
            @PathVariable("commentId") Long commentId,
            Authentication authentication
    ){
        commentService.deleteComment(commentId, authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("댓글 삭제 완료"));
    }
    //답글 수정 요청
    @PutMapping("/qna/{qnaId}/comment/{commentId}")
    @Operation(summary = "댓글 수정 요청" , description = "")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 수정 완료"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "QNA ID가 유효하지 않음"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Authentication 내 유저정보 없음")
    })
    public ApiResponse<ResponseDto> modifyComment(
            @PathVariable("qnaId") Long qnaId,
            @PathVariable("commentId") Long commentId,
            @RequestBody CommentModifyRequestDto dto,
            Authentication authentication
            ){
        commentService.modifyComment(qnaId,commentId,dto,authentication);
        return new ApiResponse<>(HttpStatus.OK,ResponseDto.getInstance("댓글 수정 완료"));
    }


}
