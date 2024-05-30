package com.jpms.codinggame.service;

import com.jpms.codinggame.dto.CommentCreateRequestDto;
import com.jpms.codinggame.dto.CommentModifyRequestDto;
import com.jpms.codinggame.dto.CommentResponseDto;
import com.jpms.codinggame.entity.Comment;
import com.jpms.codinggame.entity.User;
import com.jpms.codinggame.repository.CommentRepository;
import com.jpms.codinggame.repository.QnaRepository;
import com.jpms.codinggame.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

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
        commentRepository.save(Comment
                .builder()
                .content(dto.getContent())
                .time(dto.getTime())
                .user(userRepository.findById((Long)authentication.getPrincipal()).orElseThrow(RuntimeException::new))
                .qna(qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new))
                .build());
    }
    //조회 (QNA 에 매핑 된 댓글 가져오기)
    public List<CommentResponseDto> getCommentList(Long qnaId){
        List<Comment> commentList = commentRepository.findAllByQnaId(qnaId);
        return commentList
                .stream()
                .map(comment -> CommentResponseDto
                        .builder()
                        .content(comment.getContent())
                        .time(comment.getTime())
                        .build())
                .collect(Collectors.toList());
    }
    //수정 (authentication 확인)
    public void modifyComment(
            Long qnaId,
            Long commentId,
            CommentModifyRequestDto dto,
            Authentication authentication){
        commentRepository.save(Comment
                .builder()
                .id(commentId)
                .content(dto.getContent())
                .time(dto.getTime())
                .user(userRepository.findById((Long) authentication.getPrincipal()).orElseThrow(RuntimeException::new))
                .qna(qnaRepository.findById(qnaId).orElseThrow(RuntimeException::new))
                .build());
    }
    //삭제 (authentication 확인)
    public void deleteComment(
            Long commentId,
            Authentication authentication){
        User user1 = userRepository.findById((Long) authentication.getPrincipal()).orElseThrow(RuntimeException::new);
        User user2 = commentRepository.findById(commentId).get().getUser();
        if(user1 != user2) throw new RuntimeException();
        commentRepository.deleteById(commentId);
    }
}
