package com.jpms.codinggame.service;

import com.jpms.codinggame.Oauth2.PrincipalDetails;
import com.jpms.codinggame.dto.CommentCreateRequestDto;
import com.jpms.codinggame.dto.CommentModifyRequestDto;
import com.jpms.codinggame.dto.CommentResDto;
import com.jpms.codinggame.entity.Comment;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.CommentRepository;
import com.jpms.codinggame.repository.QnaRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final QnaRepository qnaRepository;
    private final UserRepository userRepository;

    //생성 (authentication 확인)
    public void createComment(
            Long qnaId,
            CommentCreateRequestDto dto,
            Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        commentRepository.save(Comment
                .builder()
                .content(dto.getContent())
                .time(LocalDate.now())
                .user(user)
                .qna(qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new))
                .build());
    }
    //조회 (QNA 에 매핑 된 댓글 가져오기)
    public List<CommentResDto> getCommentList(Long qnaId){
        List<Comment> commentList = commentRepository.findAllByQnaId(qnaId);
        return commentList
                .stream()
                .map(comment -> CommentResDto
                        .builder()
                        .commentId(comment.getId())
                        .content(comment.getContent())
                        .nickname(comment.getUser().getNickName())
                        .time(LocalDate.now())
                        .build())
                .collect(Collectors.toList());
    }
    //수정 (authentication 확인)
    public void modifyComment(
            Long qnaId,
            Long commentId,
            CommentModifyRequestDto dto,
            Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        commentRepository.save(Comment
                .builder()
                .id(commentId)
                .content(dto.getContent())
                .time(LocalDate.now())
                .user(user)
                .qna(qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new))
                .build());
    }
    //삭제 (authentication 확인)
    public void deleteComment(
            Long commentId,
            Authentication authentication){
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();

        User user1 = userRepository.findById(user.getId()).orElseThrow(RuntimeException::new);
        User user2 = commentRepository.findById(commentId).get().getUser();
        if(user1 != user2) throw new RuntimeException();
        commentRepository.deleteById(commentId);
    }

    public List<CommentResDto> getRecent5Comment(){
        List<Comment> commentList = commentRepository.findTop5ByOrderByIdDesc();
        return commentList
                .stream()
                .map(comment -> CommentResDto
                        .builder()
                        .commentId(comment.getId())
                        .nickname(comment.getUser().getNickName())
                        .content(comment.getContent())
                        .build())
                .toList();
    }
}
